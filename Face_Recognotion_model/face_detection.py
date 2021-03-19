import face_recognition
import imutils
import pickle
import time
import cv2
import os
import requests
import datetime


faceCascade = cv2.CascadeClassifier('./myenv/lib/python3.8/site-packages/cv2/data/haarcascade_frontalface_alt2.xml') # path to haarcascade_frontalface_alt2.xml from cv2 path(Refer where installed)

data = pickle.loads(open('face_encodings', "rb").read())
 
print("Video capture started")
video_capture = cv2.VideoCapture(0)
ids = []

while True:

    ret, frame = video_capture.read()
  
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    
    faces = faceCascade.detectMultiScale(gray,scaleFactor=1.1,minNeighbors=5,minSize=(60, 60),flags=cv2.CASCADE_SCALE_IMAGE)

    rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

    encodings = face_recognition.face_encodings(rgb)
    names = []

    for encoding in encodings:

        matches = face_recognition.compare_faces(data["encodings"],encoding)

        name = "UNAUTHORIZED"

        if True in matches:

            matchedIdxs = [i for (i, b) in enumerate(matches) if b]
            counts = {}

            for i in matchedIdxs:

                name = "AUTHORIZED: " + data["names"][i]

                counts[name] = counts.get(name, 0) + 1

            name = max(counts, key=counts.get)
 
        print(name)
        if name != "UNAUTHORIZED":
            userId = name.split("_")[1]
            if userId not in ids:
                
                ids.append(userId)
                 r = requests.post("http://127.0.0.1:8000/api/face_recognition/recordEntry/",{"userId":userId,"entryTime":datetime.datetime.now()})

        

        names.append(name)

        for ((x, y, w, h), name) in zip(faces, names):

            if name == "UNAUTHORIZED":
                cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 0, 255), 2)
                cv2.putText(frame, name, (x, y), cv2.FONT_HERSHEY_SIMPLEX,
             0.75, (0, 0, 255), 2)
            else:
                cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
                cv2.putText(frame, name, (x, y), cv2.FONT_HERSHEY_SIMPLEX,
             0.75, (0, 255, 0), 2)
    cv2.imshow("Frame", frame)
    
    if cv2.waitKey(0) & 0xFF == ord('q'):
        break
video_capture.release()
cv2.destroyAllWindows()
