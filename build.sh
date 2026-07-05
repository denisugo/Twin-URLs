rm -rf build dist package
mkdir -p build

javac -d build ./TwinUrls.java

jar --create \
    --file build/twin_urls.jar \
    --main-class TwinUrls \
    -C build .

mkdir package
cp build/twin_urls.jar package/

mkdir -p dist

/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home/bin/jpackage \
  --input package \
  --name TwinUrls \
  --main-jar twin_urls.jar \
  --type dmg \
  --dest dist \
  --java-options "-Xmx256m"
#   --add-modules java.desktop