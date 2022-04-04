package db;

import java.sql.DriverManager;
import java.sql.SQLException;

import app.BaseFrame;

public class Setting {

	public static void main(String[] args) {
		
		try {
			var con = DriverManager.getConnection("jdbc:mysql://localhost/?serverTimezone=UTC&allowLoadLocalInfile=true", "root", "1234");
			var stmt = con.createStatement();
			
			// LOAD DATA 보안 옵션 설정
			stmt.execute("SET GLOBAL local_infile= 1");
			
			// DB 존재시 제거
			stmt.execute("DROP SCHEMA IF EXISTS `2022지방_1`");
			// DB 생성
			stmt.execute("CREATE SCHEMA `2022지방_1` DEFAULT CHARACTER SET utf8 ;");
			
			stmt.execute("USE `2022지방_1`");
			
			// 테이블 생성
			stmt.execute("CREATE TABLE `2022지방_1`.`area` (\r\n"
					+ "  `a_no` INT NOT NULL AUTO_INCREMENT,\r\n"
					+ "  `a_name` VARCHAR(2) NULL,\r\n"
					+ "  PRIMARY KEY (`a_no`));");
			
			stmt.execute("CREATE TABLE `2022지방_1`.`cafe` (\r\n"
					+ "  `c_no` VARCHAR(10) NOT NULL,\r\n"
					+ "  `c_name` VARCHAR(20) NULL,\r\n"
					+ "  `t_no` VARCHAR(100) NULL,\r\n"
					+ "  `c_tel` VARCHAR(15) NULL,\r\n"
					+ "  `a_no` INT NULL,\r\n"
					+ "  `c_address` VARCHAR(50) NULL,\r\n"
					+ "  `c_price` INT NULL,\r\n"
					+ "  PRIMARY KEY (`c_no`),\r\n"
					+ "  INDEX `FK_cafe_a_no_idx` (`a_no` ASC) VISIBLE,\r\n"
					+ "  CONSTRAINT `FK_cafe_a_no`\r\n"
					+ "    FOREIGN KEY (`a_no`)\r\n"
					+ "    REFERENCES `2022지방_1`.`area` (`a_no`)\r\n"
					+ "    ON DELETE NO ACTION\r\n"
					+ "    ON UPDATE NO ACTION);");
			
			stmt.execute("CREATE TABLE `2022지방_1`.`genre` (\r\n"
					+ "  `g_no` INT NOT NULL AUTO_INCREMENT,\r\n"
					+ "  `g_name` VARCHAR(10) NULL,\r\n"
					+ "  PRIMARY KEY (`g_no`));");
			
			stmt.execute("CREATE TABLE `map` (\r\n"
					+ "	`a_no` INT NULL,\r\n"
					+ "	`m_x` INT NULL,\r\n"
					+ "	`m_y` INT NULL,\r\n"
					+ "	CONSTRAINT `FK_map_a_no` FOREIGN KEY (`a_no`) REFERENCES `area` (`a_no`) ON UPDATE CASCADE ON DELETE CASCADE\r\n"
					+ ")\r\n"
					+ "COLLATE='utf8_general_ci'\r\n"
					+ ";");
			
			stmt.execute("CREATE TABLE `ping` (\r\n"
					+ "	`a_no` INT NULL,\r\n"
					+ "	`p_x` INT NULL,\r\n"
					+ "	`p_y` INT NULL,\r\n"
					+ "	CONSTRAINT `FK_ping_a_no` FOREIGN KEY (`a_no`) REFERENCES `area` (`a_no`) ON UPDATE CASCADE ON DELETE CASCADE\r\n"
					+ ")\r\n"
					+ "COLLATE='utf8_general_ci'\r\n"
					+ ";");
			
			stmt.execute("CREATE TABLE `user` (\r\n"
					+ "	`u_no` INT NOT NULL AUTO_INCREMENT,\r\n"
					+ "	`u_id` VARCHAR(10) NULL DEFAULT NULL,\r\n"
					+ "	`u_pw` VARCHAR(10) NULL DEFAULT NULL,\r\n"
					+ "	`u_name` VARCHAR(10) NULL DEFAULT NULL,\r\n"
					+ "	`u_date` DATE NULL,\r\n"
					+ "	PRIMARY KEY (`u_no`)\r\n"
					+ ")\r\n"
					+ "COLLATE='utf8_general_ci'\r\n"
					+ ";");
			
			stmt.execute("CREATE TABLE `notice` (\r\n"
					+ "	`n_no` INT NOT NULL AUTO_INCREMENT,\r\n"
					+ "	`u_no` INT NOT NULL DEFAULT '0',\r\n"
					+ "	`n_date` DATE NOT NULL,\r\n"
					+ "	`n_title` VARCHAR(20) NOT NULL DEFAULT '0',\r\n"
					+ "	`n_content` VARCHAR(150) NOT NULL DEFAULT '0',\r\n"
					+ "	`n_viewcount` INT NOT NULL DEFAULT '0',\r\n"
					+ "	`n_open` INT NOT NULL DEFAULT '0',\r\n"
					+ "	PRIMARY KEY (`n_no`),\r\n"
					+ "	CONSTRAINT `FK_notice_u_no` FOREIGN KEY (`u_no`) REFERENCES `user` (`u_no`) ON UPDATE CASCADE ON DELETE CASCADE\r\n"
					+ ")\r\n"
					+ "COLLATE='utf8_general_ci'\r\n"
					+ ";");
			
			stmt.execute("CREATE TABLE `quiz` (\r\n"
					+ "	`q_no` INT NOT NULL AUTO_INCREMENT,\r\n"
					+ "	`q_answer` VARCHAR(10) NULL DEFAULT NULL,\r\n"
					+ "	PRIMARY KEY (`q_no`)\r\n"
					+ ")\r\n"
					+ "COLLATE='utf8_general_ci'\r\n"
					+ ";");
			
			stmt.execute("CREATE TABLE `theme` (\r\n"
					+ "	`t_no` INT NOT NULL AUTO_INCREMENT,\r\n"
					+ "	`t_name` VARCHAR(30) NULL DEFAULT NULL,\r\n"
					+ "	`g_no` INT NULL,\r\n"
					+ "	`t_explan` VARCHAR(200) NULL DEFAULT NULL,\r\n"
					+ "	`t_personnel` INT NULL,\r\n"
					+ "	`t_time` INT NULL,\r\n"
					+ "	PRIMARY KEY (`t_no`),\r\n"
					+ "	CONSTRAINT `FK_theme_g_no` FOREIGN KEY (`g_no`) REFERENCES `genre` (`g_no`) ON UPDATE CASCADE ON DELETE CASCADE\r\n"
					+ ")\r\n"
					+ "COLLATE='utf8_general_ci'\r\n"
					+ ";");
			
			stmt.execute("CREATE TABLE `reservation` (\r\n"
					+ "	`r_no` INT NOT NULL AUTO_INCREMENT,\r\n"
					+ "	`u_no` INT NULL,\r\n"
					+ "	`c_no` VARCHAR(10) NULL DEFAULT NULL,\r\n"
					+ "	`t_no` INT NULL,\r\n"
					+ "	`r_date` DATE NULL,\r\n"
					+ "	`r_time` VARCHAR(20) NULL DEFAULT NULL,\r\n"
					+ "	`r_people` INT NULL,\r\n"
					+ "	`r_attend` INT NULL,\r\n"
					+ "	PRIMARY KEY (`r_no`),\r\n"
					+ "	CONSTRAINT `FK_reservation_u_no` FOREIGN KEY (`u_no`) REFERENCES `user` (`u_no`) ON UPDATE CASCADE ON DELETE CASCADE,\r\n"
					+ "	CONSTRAINT `FK_reservation_c_no` FOREIGN KEY (`c_no`) REFERENCES `cafe` (`c_no`) ON UPDATE CASCADE ON DELETE CASCADE,\r\n"
					+ "	CONSTRAINT `FK_reservation_t_no` FOREIGN KEY (`t_no`) REFERENCES `theme` (`t_no`) ON UPDATE CASCADE ON DELETE CASCADE\r\n"
					+ ")\r\n"
					+ "COLLATE='utf8_general_ci'\r\n"
					+ ";");
			
			var tableList = "area,cafe,genre,map,ping,user,notice,quiz,theme,reservation".split(",");
			
			for (String table : tableList) {
				stmt.execute("LOAD DATA LOCAL INFILE 'datafiles/" + table + ".txt' INTO TABLE " + table + " IGNORE 1 LINES");
			}
			
			stmt.execute("DROP USER IF EXISTS 'user'@'%'");
			stmt.execute("CREATE USER 'user'@'%' IDENTIFIED BY '1234'");
			stmt.execute("GRANT SELECT, INSERT, UPDATE, DELETE ON 2022지방_1.* TO 'user'@'%'");
			
			BaseFrame.iMsg("셋팅 성공");
		} catch (Exception e) {
			e.printStackTrace();
			BaseFrame.eMsg("셋팅 실패");
			
		}
	}

}
