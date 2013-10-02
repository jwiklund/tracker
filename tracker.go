package main

import (
       "github.com/jwiklund/tracker/server"
       "os"
)

func main() {
     server.Run(os.Getenv("HOST"), os.Getenv("PORT"))
}
