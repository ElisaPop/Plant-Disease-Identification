# Plant-Disease-Identification

This project consists of an Android application that integrates a machine learning model in order to identify plant diseases.

## Architecture

This is the general architecture of the model. It consists of the model training and application part, tied together with TensorflowLite.
After training, the model is exported as a tflite file and then added to the application's resources folder, where it can be used locally.

![image](https://drive.google.com/uc?export=view&id=1xJ07x6EilwBZ1h28krwmnrjn8qiioiv2)

## Model

The model is trained with a combination of 5 datesets found online, in order to improve its accuracy on real world examples.
The architecture of the CNN was inspired by AlexNet, and the model was trained from scratch. The final accuracy is 95.21% for training, 93.46% for validation.

![image](https://drive.google.com/uc?export=view&id=1W-Z5LFRoqLYqeDfBHCd0GRx0XAaAy9YT)

## Application


![image](https://drive.google.com/uc?export=view&id=1YAp2mZosS4f7IGPdXOVkl27IxHOwNrBL)

![image](https://drive.google.com/uc?export=view&id=1ZlKs6gt-5aoWPKHcFhDvp2ZRX3htqlL1)
