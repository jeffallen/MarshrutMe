# MarshrutMe

This is an Android app under development which will help you find routes through public transport systems
in places without formal coverage by systems like Google Maps. For example, the marshrutka routes in Osh,
Kyrgyzstan are not published anywhere that I know of. Using this app, you can already find the nearest route
to you.

In the future, you will be able to record your journeys on routes that are as-yet unknown to the app, and
submit them to a server. Other users of the app will submit their journeys as well, and the server will integrate
all of these tracks into an estimation of the true route.

# routedb

This app uses gomobile/gobind in order to allow me to develop the non-UI parts of it in Go.

Use "go get github.com/jeffallen/routedb" to get the Go part of this app.

# flatbuffers

Gobind has very strict limits on the types that can cross from Go to Java (and vice versa). A nice typesafe
solution to this, at the cost of a bit of performance, is to serialize and deserialize the data structures
from Go to Java.

The state of the art in typesafe and also high performance serialization is Google Flatbuffers, so I'm using that.
