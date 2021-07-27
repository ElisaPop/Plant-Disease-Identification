import os, shutil

ALL_DIR = "C:\\Users\\elisa\\Desktop\\License_conda\\alternative_dataset\\all"
TRAIN_DIR = "C:\\Users\\elisa\\Desktop\\License_conda\\alternative_dataset\\train"
VALIDATION_DIR = "C:\\Users\\elisa\\Desktop\\License_conda\\alternative_dataset\\validation"

#test to see if it's working
os.chdir(TRAIN_DIR)
os.system('mkdir ' + 'a')

#In order to do the 80-20 split, all the files were copied to both the training folder and the validation folder.
        
list_files(ALL_DIR)

plant_dirs = os.listdir(ALL_DIR)

for entry in plant_dirs:
    entries = os.listdir(ALL_DIR+"\\"+entry)
    cnt = 0
    imgs = os.listdir(ALL_DIR + "\\" + entry)
    
    #Every 8 out of 10 images are removed from validation, while 2 out of 10 images are removed from training.
    
    for img in imgs:
    #splitting 80-20
        if cnt % 10 < 8:
            os.remove(VALIDATION_DIR + "\\" + entry + "\\" + img)
        else:
            os.remove(TRAIN_DIR + "\\" + entry + "\\" + img)
        cnt = cnt + 1
    
    
valid = 1
for entry in plant_dirs:
        
    imgs = os.listdir(ALL_DIR + "\\" + entry )
    imgs_train = os.listdir(TRAIN_DIR + "\\" + entry)
    imgs_validation = os.listdir(VALIDATION_DIR + "\\" + entry)
        
    total = len(imgs_train) + len(imgs_validation)
    
    if len(imgs) != total :
        valid = 0

print(valid)