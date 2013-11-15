sed -i 's/DefaultMasterPublicIP/'"$1"'/g' /home/ubuntu/Project/assersact-final/awseract/awseract-core/conf/masterInfo
sed -i 's/DefaultPublicIP/'"$2"'/g' /home/ubuntu/Project/assersact-final/awseract/awseract-core/conf/masterInfo
sed -i 's/DefaultInstanceID/'"$3"'/g' /home/ubuntu/Project/assersact-final/awseract/awseract-core/conf/masterInfo
sed -i 's/DefaultMasterPublicIP/'"$1"'/g' /home/ubuntu/Project/assersact-final/awseract/awseract-core/src/main/resources/application.conf
sed -i 's/DefaultPublicIP/'"$1"'/g' /home/ubuntu/Project/assersact-final/awseract/awseract-core/src/main/resources/application.conf
