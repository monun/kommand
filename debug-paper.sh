#!/bin/bash

server='https://papermc.io/api/v1/paper/1.17.1/latest/download'
plugins=(
  'https://github.com/monun/auto-reloader/releases/latest/download/AutoReloader.jar'
)

script=$(basename "$0")
server_folder=".${script%.*}"
mkdir -p "$server_folder"

start_script="start.sh"
start_config="$start_script.conf"

if [ ! -f "$server_folder/$start_script" ]; then
  if [ -f ".server/$start_script" ]; then
    cp ".server/$start_script" "$server_folder/$start_script"
  else
    wget -qc -P "$server_folder" -N "https://raw.githubusercontent.com/monun/server-script/master/.server/$start_script"
    wget -qc -P "$server_folder" -N "https://raw.githubusercontent.com/monun/server-script/master/.server/start.bat"
  fi
fi

cd "$server_folder" || exit

if [ ! -f "$start_config" ]; then
  cat <<EOF >$start_config
server=$server
debug=true
debug_port=5005
backup=false
force_restart=false
memory=16
plugins=(
EOF
  for plugin in "${plugins[@]}"; do
    echo "  \"$plugin\"" >>$start_config
  done
  echo ")" >>$start_config
fi

chmod +x ./$start_script
./$start_script launch
