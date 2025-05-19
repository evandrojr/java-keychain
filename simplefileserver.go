package main

import (
	"fmt"
	"log"
	"net/http"
	"os"
)

func main() {
	port := 2222
	cwd, err := os.Getwd()
	if err != nil {
		log.Fatalf("Erro ao obter diretório atual: %v", err)
	}
	fs := http.FileServer(http.Dir(cwd))
	http.Handle("/", fs)
	fmt.Printf("Servidor rodando em http://localhost:%d/\n", port)
	log.Fatal(http.ListenAndServe(fmt.Sprintf(":%d", port), nil))
}
