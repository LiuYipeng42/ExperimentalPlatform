import sys
import traceback

import pymysql
import pandas as pd
from pymysql import IntegrityError

args = sys.argv

fileName = args[1]
classNum = args[2]
teacherId = args[3]
identity = args[4]

db = pymysql.connect(host='localhost', user='root', password='121522734a', port=3306, db='ExperimentalPlatform')
cursor = db.cursor()

df = pd.read_excel(fileName)

start_row = 0

def print_exception():
    exception = ""
    value, tb = sys.exc_info()[1:]
    for line in traceback.TracebackException(type(value), value, tb, limit=None).format(chain=True):
        exception += line
    print(exception)


if identity == "admin":
    if df.columns.values[1] != "Unnamed: 1":
        cursor.execute(
            "select id from user where account=%s", (df.columns.values[1])
        )
        teacherId = cursor.fetchone()[0]
    start_row = 1
    

for row in range(start_row, df.shape[0]):

    studentNo = int(df.loc[row].values[0])
    studentName = df.loc[row].values[1]

    try:
        try:
            cursor.execute(
                "insert into user (account, password, name, identity) values (%s, %s, %s, %s)",
                (studentNo, studentNo, studentName, "guet_student")
            )
            studentId = cursor.lastrowid
        except IntegrityError:
            cursor.execute("select id from user where account=%s", studentNo)
            studentId = cursor.fetchall()[0][0]

        try:
            cursor.execute(
                "insert into class (class_num, teacher_id) values (%s, %s)",
                (classNum, teacherId)
            )
            classId = cursor.lastrowid
        except IntegrityError:
            cursor.execute("select id from class where class_num=%s", classNum)
            classId = cursor.fetchall()[0][0]

        cursor.execute(
            "insert into student_class (student_id, class_id) values (%s, %s)",
            (studentId, classId)
        )
    except:
        print_exception()


db.commit()
