#!/usr/bin/env bash

# 애플리케이션 루트 경로
PROJECT_ROOT="/home/ubuntu/app"

# 실행할 jar 파일 경로
JAR_FILE="$PROJECT_ROOT/build/libs/*SNAPSHOT.jar"

# 애플리케이션의 로그를 저장할 파일 경로
APP_LOG="$PROJECT_ROOT/application.log"

# 애플리케이션의 에러 로그를 저장할 파일 경로
ERROR_LOG="$PROJECT_ROOT/error.log"

# 배포 스크립트 실행 로그를 저장할 파일 경로
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# build 파일 복사
echo "$TIME_NOW > $JAR_FILE 파일 복사" >> $DEPLOY_LOG
cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE

# jar 파일 실행
echo "$TIME_NOW > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
nohup java -jar $JAR_FILE > $APP_LOG 2> $ERROR_LOG &

# 각 단계의 작업 내용과 실행된 프로세스 기록
CURRENT_PID=$(pgrep -f $JAR_FILE)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG