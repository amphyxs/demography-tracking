set -e

cd "$HOME/haproxy-3.1.9" || exit 1

cfg="$HOME/haproxy-generated.cfg"
pid_file="$HOME/haproxy.pid"
log_file="$HOME/haproxy.log"

# Проверяем наличие PID и жив ли процесс
pid=""
if [ -f "$pid_file" ]; then
  pid=$(cat "$pid_file" 2>/dev/null || echo "")
  if [ -n "$pid" ] && ! ps -p "$pid" > /dev/null 2>&1; then
    echo "$(date '+%F %T') Stale PID ($pid) found — process not running, removing pid file" >> "$log_file"
    rm -f "$pid_file"
    pid=""
  fi
fi

# Запускаем HAProxy
if [ -n "$pid" ]; then
  echo "$(date '+%F %T') Reloading HAProxy (old pid=$pid)" >> "$log_file"
  nohup ./haproxy -f "$cfg" -p "$pid_file" -sf "$pid" >> "$log_file" 2>&1 &
else
  echo "$(date '+%F %T') Starting new HAProxy" >> "$log_file"
  nohup ./haproxy -f "$cfg" -p "$pid_file" >> "$log_file" 2>&1 &
fi

sleep 1

# Проверяем, что процесс действительно запущен
new_pid=$(pgrep -n haproxy || true)
if [ -z "$new_pid" ]; then
  echo "$(date '+%F %T') ERROR: HAProxy did not start properly!" >> "$log_file"
  exit 1
else
  echo "$new_pid" > "$pid_file"
  echo "$(date '+%F %T') HAProxy is running with PID $new_pid" >> "$log_file"
fi
