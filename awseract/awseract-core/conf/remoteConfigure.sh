# $1 = IP Address of the target machine
# $2 = the location of the private key file

# Make sure that the private key file is write protected or something
chmod 400 $2

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i $2 ubuntu@$1 '

~/Project/assersact-final/awseract/awseract-core/conf/configure.sh '"$3"' '"$4"' '"$5"'

 '