package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.discussio.resourc.mapper.auto.BackupConfigMapper;
import com.discussio.resourc.mapper.auto.BackupRecordMapper;
import com.discussio.resourc.model.auto.BackupConfig;
import com.discussio.resourc.model.auto.BackupRecord;
import com.discussio.resourc.service.IBackupService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 备份服务实现：uploads 文件夹 + 数据库导出为 SQL，打包为 zip
 */
@Service
public class BackupServiceImpl implements IBackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupServiceImpl.class);

    @Autowired
    private BackupConfigMapper configMapper;
    @Autowired
    private BackupRecordMapper recordMapper;
    @Autowired
    private DataSource dataSource;

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @Override
    public BackupConfig getConfig() {
        BackupConfig c = configMapper.selectById(1L);
        if (c == null) {
            c = new BackupConfig();
            c.setId(1L);
            c.setAutoEnabled(0);
            c.setScheduleCron("0 0 2 * * ?");
            c.setScheduleTime("02:00");
            c.setRetentionDays(30);
            c.setIncludeUploads(1);
            c.setIncludeDatabase(1);
        }
        return c;
    }

    @Override
    public int saveConfig(BackupConfig config) {
        config.setUpdateTime(new Date());
        if (config.getId() == null) config.setId(1L);
        if (config.getScheduleTime() != null && !config.getScheduleTime().isEmpty()) {
            String[] parts = config.getScheduleTime().trim().split(":");
            int h = parts.length >= 1 ? Math.max(0, Math.min(23, parseInt(parts[0], 2))) : 2;
            int m = parts.length >= 2 ? Math.max(0, Math.min(59, parseInt(parts[1], 0))) : 0;
            config.setScheduleCron("0 " + m + " " + h + " * * ?");
        }
        if (configMapper.selectById(config.getId()) != null) {
            return configMapper.updateById(config);
        }
        config.setCreateTime(new Date());
        return configMapper.insert(config);
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    @Override
    public BackupRecord executeBackup(String targetPath, boolean includeUploads, boolean includeDatabase, boolean isAuto) {
        long start = System.currentTimeMillis();
        BackupRecord record = new BackupRecord();
        record.setBackupType(isAuto ? "auto" : "manual");
        record.setCreateTime(new Date());
        record.setStatus("failed");

        if (StringUtils.isBlank(targetPath)) {
            targetPath = System.getProperty("user.home") + File.separator + "surgilearn_backups";
        }
        File backupDir = new File(targetPath);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        if (!backupDir.isDirectory() || !backupDir.canWrite()) {
            record.setErrorMsg("备份路径不可写: " + targetPath);
            recordMapper.insert(record);
            return record;
        }
        if (!includeUploads && !includeDatabase) {
            record.setErrorMsg("请至少选择备份 uploads 或数据库其一");
            recordMapper.insert(record);
            return record;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String zipName = "surgilearn_backup_" + timestamp + ".zip";
        Path zipPath = Paths.get(targetPath, zipName);
        Path tempDir = null;

        try {
            tempDir = Files.createTempDirectory("surgilearn_backup_");
            Path workDir = tempDir;

            if (includeUploads) {
                Path uploadsSrc = Paths.get(uploadPath).toAbsolutePath();
                if (Files.exists(uploadsSrc) && Files.isDirectory(uploadsSrc)) {
                    Path uploadsDest = workDir.resolve("uploads");
                    Files.createDirectories(uploadsDest);
                    copyDirectory(uploadsSrc, uploadsDest);
                }
            }

            if (includeDatabase) {
                Path sqlFile = workDir.resolve("database.sql");
                exportDatabase(sqlFile);
            }

            zipDirectory(workDir, zipPath);
            long size = Files.size(zipPath);
            long duration = (System.currentTimeMillis() - start) / 1000;

            record.setFileName(zipName);
            record.setFilePath(zipPath.toAbsolutePath().toString());
            record.setFileSize(size);
            record.setStatus("success");
            record.setDurationSeconds((int) duration);
        } catch (Exception e) {
            log.error("备份失败", e);
            record.setErrorMsg(e.getMessage());
        } finally {
            if (tempDir != null) {
                try {
                    deleteRecursively(tempDir);
                } catch (Exception ignored) {}
            }
        }
        record.setDurationSeconds((int) ((System.currentTimeMillis() - start) / 1000));
        recordMapper.insert(record);
        return record;
    }

    private void copyDirectory(Path src, Path dest) throws IOException {
        Files.walk(src).forEach(source -> {
            try {
                Path target = dest.resolve(src.relativize(source));
                if (Files.isDirectory(source)) {
                    if (!Files.exists(target)) Files.createDirectories(target);
                } else {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    /**
     * 使用 JDBC 纯 Java 导出数据库，不依赖 mysqldump
     */
    private void exportDatabase(Path sqlFile) throws Exception {
        try (Connection conn = dataSource.getConnection();
             BufferedWriter writer = Files.newBufferedWriter(sqlFile, StandardCharsets.UTF_8)) {

            writer.write("-- SurgiLearn 数据库备份 - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
            writer.write("SET NAMES utf8mb4;\nSET FOREIGN_KEY_CHECKS = 0;\n\n");

            DatabaseMetaData meta = conn.getMetaData();
            String catalog = conn.getCatalog();
            String schema = null;
            String[] types = {"TABLE"};

            try (ResultSet tables = meta.getTables(catalog, schema, "%", types)) {
                List<String> tableNames = new ArrayList<>();
                while (tables.next()) {
                    String name = tables.getString("TABLE_NAME");
                    if (name != null && !name.startsWith("sys_")) {
                        tableNames.add(name);
                    }
                }
                for (String tableName : tableNames) {
                    exportTable(conn, writer, tableName);
                }
            }
            writer.write("SET FOREIGN_KEY_CHECKS = 1;\n");
        }
    }

    private void exportTable(Connection conn, BufferedWriter writer, String tableName) throws Exception {
        writer.write("-- ----------------------------\n");
        writer.write("-- Table structure for " + tableName + "\n");
        writer.write("-- ----------------------------\n");
        writer.write("DROP TABLE IF EXISTS `" + tableName + "`;\n");

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SHOW CREATE TABLE `" + tableName + "`")) {
            if (rs.next()) {
                String ddl = rs.getString(2);
                if (ddl != null) {
                    writer.write(ddl + ";\n\n");
                }
            }
        }

        writer.write("-- ----------------------------\n");
        writer.write("-- Records of " + tableName + "\n");
        writer.write("-- ----------------------------\n");

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM `" + tableName + "`")) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            List<String> colNames = new ArrayList<>();
            for (int i = 1; i <= cols; i++) {
                colNames.add("`" + rsmd.getColumnName(i) + "`");
            }
            String colList = String.join(", ", colNames);
            int rowCount = 0;
            while (rs.next()) {
                List<String> values = new ArrayList<>();
                for (int i = 1; i <= cols; i++) {
                    Object val = rs.getObject(i);
                    values.add(formatSqlValue(val));
                }
                writer.write("INSERT INTO `" + tableName + "` (" + colList + ") VALUES (" + String.join(", ", values) + ");\n");
                rowCount++;
            }
            if (rowCount > 0) writer.write("\n");
        }
    }

    private String formatSqlValue(Object val) {
        if (val == null) return "NULL";
        if (val instanceof Number) return String.valueOf(val);
        if (val instanceof Boolean) return ((Boolean) val) ? "1" : "0";
        if (val instanceof Date) {
            return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) val) + "'";
        }
        if (val instanceof byte[]) return "0x" + bytesToHex((byte[]) val);
        String s = val.toString();
        return "'" + s.replace("\\", "\\\\").replace("'", "''").replace("\n", "\\n").replace("\r", "\\r") + "'";
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    private void zipDirectory(Path dir, Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(dir).filter(p -> !Files.isDirectory(p)).forEach(p -> {
                try {
                    String entryName = dir.relativize(p).toString().replace("\\", "/");
                    zos.putNextEntry(new ZipEntry(entryName));
                    Files.copy(p, zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path p : stream) deleteRecursively(p);
            }
        }
        Files.delete(path);
    }

    @Override
    public List<BackupRecord> listRecords(int page, int limit) {
        Page<BackupRecord> p = new Page<>(page, limit);
        QueryWrapper<BackupRecord> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time");
        return recordMapper.selectPage(p, qw).getRecords();
    }

    @Override
    public long countRecords() {
        return recordMapper.selectCount(null);
    }

    @Override
    public int deleteRecord(Long id) {
        BackupRecord r = recordMapper.selectById(id);
        if (r != null && r.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(r.getFilePath()));
            } catch (IOException ignored) {}
        }
        return recordMapper.deleteById(id);
    }

    @Override
    public void cleanupByRetention(int retentionDays) {
        if (retentionDays <= 0) return;
        Date cutoff = new Date(System.currentTimeMillis() - (long) retentionDays * 24 * 60 * 60 * 1000);
        QueryWrapper<BackupRecord> qw = new QueryWrapper<>();
        qw.lt("create_time", cutoff);
        List<BackupRecord> old = recordMapper.selectList(qw);
        for (BackupRecord r : old) {
            deleteRecord(r.getId());
        }
    }
}
