for i in {1..50}; do
	    curl -I -k https://localhost:28450/api/proxy/demography/hair-color/BROWN 2>res 1>res &
    done
