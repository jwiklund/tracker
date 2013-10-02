package server

import (
	"fmt"
	"github.com/emicklei/go-restful"
	"github.com/jwiklund/tracker/rss"
	"github.com/jwiklund/tracker/source"
	"log"
	"net/http"
)

func Tracker() *restful.WebService {
	ws := new(restful.WebService)
	ws.Path("/")
	ws.Route(ws.GET("/questionable").To(questionable)).Produces(restful.MIME_XML)
	return ws
}

func questionable(request *restful.Request, response *restful.Response) {
	q := source.NewQuestionable()
	response.Write([]byte("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>"))
	response.Write([]byte("<rss xmlns:atom=\"http://www.w3.org/2005/Atom\" version=\"2.0\">"))
	rss.Write(q, response)
	response.Write([]byte("</rss>"))
}

func Run(ip, port string) {
	if port == "" {
		port = "8080"
	}
	list := fmt.Sprintf("%s:%s", ip, port)
	restful.Add(Tracker())
	log.Printf("start listening on " + list)
	log.Fatal(http.ListenAndServe(list, nil))
}
