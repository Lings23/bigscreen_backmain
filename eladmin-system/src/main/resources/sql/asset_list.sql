-- 资产列表表
CREATE TABLE asset_list (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    asset_ip VARCHAR(45) NOT NULL COMMENT '资产IP地址',
    asset_port INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '资产端口，默认0',
    system_name VARCHAR(100) DEFAULT NULL COMMENT '资产系统名称',
    organization_name VARCHAR(100) DEFAULT NULL COMMENT '所属单位',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '资产创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '资产更新时间',
    UNIQUE KEY uniq_ip_port (asset_ip, asset_port)
) COMMENT='资产列表表';

-- 插入示例数据
INSERT INTO asset_list (asset_ip, asset_port, system_name, organization_name) VALUES
('192.168.1.100', 80, 'Web服务器', '技术部'),
('192.168.1.101', 22, 'SSH服务器', '运维部'),
('192.168.1.102', 3306, '数据库服务器', '数据部'),
('192.168.1.103', 8080, '应用服务器', '开发部'),
('192.168.1.104', 443, 'HTTPS服务器', '安全部'); 