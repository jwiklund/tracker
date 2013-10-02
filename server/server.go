package main

import (
	"github.com/emicklei/go-restful"
	"github.com/jwiklund/tracker/rss"
	"github.com/jwiklund/tracker/source"
	"log"
	"net/http"
)

func Tracker() *restful.WebService {
	ws := new(restful.WebService)
	ws.Path("/").Produces(restful.MIME_XML)
	ws.Route(ws.GET("/questionable").To(questionable))
	return ws
}

func questionable(request *restful.Request, response *restful.Response) {
	q := source.NewQuestionable()
	rss.Write(q, response)
}

func main() {
	restful.Add(Tracker())
	log.Printf("start listening on localhost:8080")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
