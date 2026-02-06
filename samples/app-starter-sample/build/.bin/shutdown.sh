#!/bin/sh
cat app.pid|xargs kill
rm -f nohup.out
