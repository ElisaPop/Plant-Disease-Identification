import tensorflow as tf
from tensorflow import keras
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Flatten, Conv2D, MaxPooling2D, BatchNormalization, Dropout
from tensorflow.keras.preprocessing.image import ImageDataGenerator

import numpy as np
import matplotlib.pyplot as plt

tf.config.list_physical_devices('GPU')
tf.compat.v1.Session(config=tf.compat.v1.ConfigProto(log_device_placement=True))

IMG_SIZE = 224
BATCH_SIZE = 147
EPOCHS = 30

TRAIN_DIR = 'C:\\Users\\elisa\\Desktop\\License_conda\\alternative_dataset\\train'
VALID_DIR = 'C:\\Users\\elisa\\Desktop\\License_conda\\alternative_dataset\\validation'

image_datagen = ImageDataGenerator(
    rescale=1 / 255.0,
    rotation_range=5,
    zoom_range=0.1,
    shear_range=0.05,
    horizontal_flip=True,
    validation_split=0.2
)
IMAGE = (224, 224)

train_generator = image_datagen.flow_from_directory(directory=TRAIN_DIR, shuffle=True,
                                                    target_size=(IMG_SIZE, IMG_SIZE), batch_size=BATCH_SIZE,
                                                    class_mode='categorical')

validation_generator = image_datagen.flow_from_directory(directory=VALID_DIR, shuffle=True,
                                                         target_size=(IMG_SIZE, IMG_SIZE),
                                                         class_mode='categorical')

plant_model = Sequential()


plant_model.add(Conv2D(filters=32,kernel_size=(2,2),strides=1, activation='relu',input_shape=(IMG_SIZE, IMG_SIZE, 3)))
plant_model.add(MaxPooling2D(pool_size=(3,3),strides=(2,2)))

plant_model.add(BatchNormalization())
plant_model.add(Conv2D(filters=64,kernel_size=(3,3),strides=1, activation='relu'))
plant_model.add(Conv2D(filters=64,kernel_size=(3,3),strides=1, activation='relu'))
plant_model.add(MaxPooling2D(pool_size=(3,3),strides=(2,2)))

plant_model.add(BatchNormalization())
plant_model.add(Conv2D(filters=128,kernel_size=(3,3),strides=1, activation='relu'))
plant_model.add(Conv2D(filters=128,kernel_size=(3,3),strides=2, activation='relu'))
plant_model.add(MaxPooling2D(pool_size=(3,3),strides=(2,2)))

plant_model.add(BatchNormalization())
plant_model.add(Conv2D(filters=256,kernel_size=(3,3),strides=1, activation='relu'))
plant_model.add(Conv2D(filters=256,kernel_size=(3,3),strides=2, activation='relu'))
plant_model.add(MaxPooling2D(pool_size=(2,2),strides=(2,2)))


plant_model.add(BatchNormalization())
plant_model.add(Conv2D(filters=512,kernel_size=(2,2),strides=1, activation='relu'))
plant_model.add(MaxPooling2D(pool_size=(1,1),strides=(2,2)))


plant_model.add(BatchNormalization())
plant_model.add(Flatten())
plant_model.add(Dropout(0.5))
plant_model.add(Dense(512, activation='relu'))
plant_model.add(Dropout(0.5))
plant_model.add(Dense(46, activation='softmax'))

plant_model.compile(optimizer='adam', loss=keras.losses.categorical_crossentropy, metrics=['accuracy'])

# train: 101091
# validation: 25226
# steps_per_epoch 687

H = plant_model.fit(train_generator, steps_per_epoch=687, epochs=EPOCHS, shuffle="true",
                    validation_data=validation_generator, validation_steps=171)

plant_model.save('C:\\Users\\elisa\\Desktop\\License_conda\\mlmodel')

TFLITE_MODEL = "C:\\Users\\elisa\\Desktop\\License_conda\\mlmodel\\plant_disease_model.tflite"

# Convert the model to standard TensorFlow Lite model
converter = tf.lite.TFLiteConverter.from_saved_model('C:\\Users\\elisa\\Desktop\\License_conda\\mlmodel')
converted_tflite_model = converter.convert()
open(TFLITE_MODEL, "wb").write(converted_tflite_model)

N = np.arange(0, EPOCHS)
plt.style.use("ggplot")
plt.figure()
plt.plot(N, H.history["val_loss"], label="val_loss")
plt.plot(N, H.history["loss"], label="train_loss")
plt.title("Training loss on dataset")
plt.xlabel("Epoch #")
plt.ylabel("Loss")
plt.legend(loc="lower left")
plt.savefig("C:\\Users\\elisa\\Desktop\\License_conda\\mlmodel\\loss.png")

plt.figure()
plt.plot(N, H.history["accuracy"], label="train_acc")
plt.plot(N, H.history["val_accuracy"], label="val_acc")
plt.title("Training accuracy on dataset")
plt.xlabel("Epoch #")
plt.ylabel("Accuracy")
plt.legend(loc="lower left")
plt.savefig("C:\\Users\\elisa\\Desktop\\License_conda\\mlmodel\\acc.png")