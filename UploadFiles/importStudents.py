import sys
import traceback

import pymysql
import pandas as pd

args = sys.argv

fileName = args[1]
classId = args[2]

db = pymysql.connect(host='localhost', user='root', password='121522734a', port=3306, db='ExperimentalPlatform')
cursor = db.cursor()

df = pd.read_excel(fileName)

# 对于每一行，通过列名name访问对应的元素
for row in range(df.shape[0]):

    studentNo = int(df.loc[row].values[1])
    studentName = df.loc[row].values[2]

    try:
        cursor.execute(
            "insert into user (account, password, name, class_id) values (%s, %s, %s, %s)",
            (studentNo, studentNo, studentName, classId)
        )
    except:
        exception = ""
        value, tb = sys.exc_info()[1:]
        for line in traceback.TracebackException(type(value), value, tb, limit=None).format(chain=True):
            exception += line
        print(exception)

db.commit()
