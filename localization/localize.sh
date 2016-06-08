#!/bin/bash

# usage: sh localize.sh <platform> <destination>
# example: sh localize.sh android .../[Project]/.../res/
# example: sh localize.sh ios .../[Project]/


# variable
PLATFORM="";
DESTINATION="";
CURRENT=$(pwd);
LOCALE_TOOL="Locale_Tool.jar";
LOCALE_FILE="Locale.xlsx";
LOCALE_STRING_PATH_IOS="ios";
LOCALE_STRING_PATH_ANDROID="android";



# function
getCountryLanguageListInIOSLocaleFolder() {
    for f in "$LOCALE_STRING_PATH_IOS"/*; do
        fileNameAndExt=$(basename $f)
        fileName="${fileNameAndExt%.*}"
        fileName=${fileName:0:5}
        countryLanguageListInIOSLocaleFolder=(${countryLanguageListInIOSLocaleFolder[@]} $fileName)
    done
}

getCountryLanguageListInIOSProject() {
    for f in "$DESTINATION"/*; do
        fileExt="${f##*.}"
        if [ "$fileExt" == "lproj" ]; then
            fileNameAndExt=$(basename "$f")
            fileName="${fileNameAndExt%.*}"
            fileName=${fileName:0:5}
            countryLanguageListInIOSProject=(${countryLanguageListInIOSProject[@]} $fileName)
        fi
    done
}

handleChangeLocaleIOS() {
    for fnLocaleFolder in ${countryLanguageListInIOSLocaleFolder[@]}; do
        isExist=0
        fnLocaleProject=""
        for (( i = 0; i < ${countryLanguageListInIOSProjectLength}; i++ )); do
            fnLocaleProject=${countryLanguageListInIOSProject[$i]}
            if [ $fnLocaleFolder == $fnLocaleProject ]; then
                let isExist=1
                break
            fi
    done

    localizeFilePathInProject=""
    localizeFilePathInLocaleFolder=""

    if [ $isExist == 1 ]; then
        localizeFilePathInProject="$DESTINATION/$fnLocaleProject.lproj"
        localizeFilePathInLocaleFolder="$LOCALE_STRING_PATH_IOS/$fnLocaleFolder.lproj/Localizable.strings"
        cp "$localizeFilePathInLocaleFolder" "$localizeFilePathInProject"
    else
        localizeFilePathInProject="$DESTINATION"
        localizeFilePathInLocaleFolder="$LOCALE_STRING_PATH_IOS/$fnLocaleFolder.lproj"
        cp -r "$localizeFilePathInLocaleFolder" "$localizeFilePathInProject"
    fi
    done
}


getCountryLanguageListInANDROIDProject() {
    for f in "$DESTINATION"/*; do
        fileNameAndExt=$(basename $f)
        fileName="${fileNameAndExt%.*}"
        if [[ "$fileName" == *"values-"* ]]; then
            countryLanguageListInANDROIDProject=(${countryLanguageListInANDROIDProject[@]} $fileName)
        fi
done
}

getCountryLanguageListInANDROIDLocaleFolder() {
for f in "$LOCALE_STRING_PATH_ANDROID"/*; do
    fileNameAndExt=$(basename $f)
    fileName="${fileNameAndExt%.*}"
    countryLanguageListInANDROIDLocaleFolder=(${countryLanguageListInANDROIDLocaleFolder[@]} $fileName)
done
}

handleChangeLocaleANDROID() {
    for fnLocaleFolder in ${countryLanguageListInANDROIDLocaleFolder[@]}; do
        isExist=0
        fnLocaleProject=""
        for (( i = 0; i < ${countryLanguageListInANDROIDProjectLength}; i++ )); do
            fnLocaleProject=${countryLanguageListInANDROIDProject[$i]}
            if [ $fnLocaleFolder == $fnLocaleProject ]; then
                let isExist=1
                break
            fi
        done

    localizeFilePathInProject=""
    localizeFilePathInLocaleFolder=""

    if [ $isExist == 1 ]; then
        localizeFilePathInProject="$DESTINATION/$fnLocaleProject"
        localizeFilePathInLocaleFolder="$LOCALE_STRING_PATH_ANDROID/$fnLocaleFolder/strings.xml"
        cp "$localizeFilePathInLocaleFolder" "$localizeFilePathInProject"
    else
        localizeFilePathInProject="$DESTINATION"
        localizeFilePathInLocaleFolder="$LOCALE_STRING_PATH_ANDROID/$fnLocaleFolder"
        cp -r "$localizeFilePathInLocaleFolder" "$localizeFilePathInProject"
    fi
done
}



# execute
if [ "$1" != "" ]; then
    PLATFORM=$1
fi

if [ "$2" != "" ]; then
    DESTINATION=$2
fi

echo "==========Current directory: "$CURRENT" =========="

if [ ! -f  $LOCALE_TOOL ]; then
    echo "==========[$LOCALE_TOOL] does not exist, failed to execute!=========="
    exit;
fi

if [ ! -f $LOCALE_FILE ]; then
    echo "==========[$LOCALE_FILE] does not exist, failed to execute!=========="
    exit;
fi

if [ -d "android" ]; then
	echo "==========Delete [android] folder...=========="
	rm -r android
fi

if [ -d "ios" ]; then
    echo "==========Delete [ios] folder...=========="
    rm -r ios
fi

java -jar Locale_Tool.jar "Locale.xlsx" "."

if [ "$PLATFORM" == "" ]; then
    echo "==========Please provide PLATFORM to copy: android or ios=========="
    exit;
fi

    echo "==========PLATFORM: $PLATFORM=========="

if [ "$PLATFORM" == "ios" ]; then
    getCountryLanguageListInIOSProject
    getCountryLanguageListInIOSLocaleFolder
    countryLanguageListInIOSProjectLength=${#countryLanguageListInIOSProject[@]}
    countryLanguageListInIOSProjectLength=${#countryLanguageListInIOSProject[@]}
    countryLanguageListInIOSLocaleFolderLength=${#countryLanguageListInIOSLocaleFolder[@]}
    handleChangeLocaleIOS
elif [ "$PLATFORM" == "android" ]; then
    getCountryLanguageListInANDROIDProject
    getCountryLanguageListInANDROIDLocaleFolder
    countryLanguageListInANDROIDProjectLength=${#countryLanguageListInANDROIDProject[@]}
    countryLanguageListInANDROIDProjectLength=${#countryLanguageListInANDROIDProject[@]}
    countryLanguageListInANDROIDLocaleFolderLength=${#countryLanguageListInANDROIDLocaleFolder[@]}
    handleChangeLocaleANDROID
fi

echo "==========Copy locale files finished=========="
exit;



