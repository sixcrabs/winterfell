@echo off
TITLE {project.name}
java -jar app/{project.name}-{project.version}.jar --spring.config.location=cfg/ --logging.file.path=logs/ --server.tomcat.basedir=tmp/