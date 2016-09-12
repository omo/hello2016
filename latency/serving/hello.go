package hello

import "net/http"
import "strconv"

func init() {
	http.HandleFunc("/", handler)
}

func handler(w http.ResponseWriter, r *http.Request) {
	var buffer [1024]byte
	for i := 0; i < 1024; i++ {
		buffer[i] = byte(i % 256)
	}

	lenText := r.URL.Query()["len"]
	if 0 == len(lenText) || 0 == len(lenText[0]) {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	blen, err := strconv.Atoi(lenText[0])
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	if blen <= 0 {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	for 0 < blen {
		if blen < 1024 {
			w.Write(buffer[0:blen])
			blen = 0
		} else {
			w.Write(buffer[:])
			blen = blen - 1024
		}
	}
}
