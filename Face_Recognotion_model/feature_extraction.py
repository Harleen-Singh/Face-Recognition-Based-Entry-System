from imutils import paths
import face_recognition
import pickle
import cv2
import os

images = list(paths.list_images('../Backend/media'))
encodings = []
names = []

for (i, images) in enumerate(images):
    
    name = images.split(os.path.sep)[-2]
    
    image = cv2.imread(images)
    rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    
    boxes = face_recognition.face_locations(rgb,model='hog')
    
    encodings = face_recognition.face_encodings(rgb, boxes)
    
    for enc in encodings:
        encodings.append(enc)
        names.append(name)

data = {"encodings": encodings, "names": names}

file = open("face_encodings", "wb")
file.write(pickle.dumps(data))
file.close()
