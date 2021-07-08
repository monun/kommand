checkLegacy() {
    [  "1.17" != "$(echo -e "1.17\n$1" | sort -V | head -n1)" ]
}

# Java heap
memory=1G
# List of versions to build
versions=(
  "1.17.1"
  "1.17"
  "1.16.5"
  "1.15.2"
  "1.14.4"
  "1.13.2"
)
# Maven local repository
maven="$HOME/.m2/repository/org/spigotmc/spigot/"

# Check already received aersions
for version in "${versions[@]}"; do
  repo=$(ls -1d "$maven$version"*"-R0.1-SNAPSHOT" 2>/dev/null)

  if [ -n "$repo" ]; then
    name=$(basename "$repo")
    jar="$repo/spigot-$name.jar"
    pom="$repo/spigot-$name.pom"

    if [ -f "$jar" ] && [ -f "$pom" ]; then
      echo "Skip build $version"
      continue
    fi
  fi

  echo "Download $version"
  downloads+=("$version")
done

# Create folder .buildtools
mkdir -p ".buildtools"
cd ".buildtools" || exit

# Download BuildTools.jar
wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar

# Install sdkman for jdk switching
curl -s "https://get.sdkman.io" | bash >> /dev/null
source "$HOME/.sdkman/bin/sdkman-init.sh" >> /dev/null

# Build
for version in "${downloads[@]}"; do
  jdk=$(checkLegacy "$version" && echo "8.0.292-zulu" || echo "16.0.1-zulu")
  echo "$jdk"
  sdk install java "$jdk" >> /dev/null
  sdk use java "$jdk"
  java -Xmx"$memory" -Xms"$memory" -jar "BuildTools.jar" --rev "$version" "--disable-java-check" "--remapped"
done
