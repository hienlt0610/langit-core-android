#!/bin/bash
echo "Updating volley network library..."
if [ -d "volley" ]; then
	echo "Volley directory already exists, updating..."
	cd volley
	git pull
	if ! [ $? -eq 0 ]; then
		echo "Failed to pull volley network library from master, please try again!"
		exit;
	fi
	cd ..
	cp -r volley/src/main/* src/main/
	wait
	echo "Update volley network library successfully!"
	exit;
fi

echo "Cloning master volley..."
git clone https://android.googlesource.com/platform/frameworks/volley
if ! [ $? -eq 0 ]; then
	echo "Failed to clone volley network library!"
	exit;
else
	echo "Clone volley network library successfully! Updating..."
	cd volley
	git pull
	cd ..
	cp -r volley/src/main/* src/main/
	echo "Update volley network library successfully!"
fi
exit;
