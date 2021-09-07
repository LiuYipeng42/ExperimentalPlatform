import sys

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
    try:

        studentNo = int(df.loc[row].values[1])
        studentName = df.loc[row].values[2]

        cursor.execute(
            "insert into student (account, password, name, class_id) values (%s, %s, %s, %s)",
            (studentNo, studentNo, studentName, classId)
        )

    except ValueError:
        pass

db.commit()
