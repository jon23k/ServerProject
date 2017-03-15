[![build status](https://ada.csse.rose-hulman.edu/kurianj/EAGM/badges/master/build.svg)](https://ada.csse.rose-hulman.edu/kurianj/EAGM/commits/master)

[![coverage report](https://ada.csse.rose-hulman.edu/kurianj/EAGM/badges/master/coverage.svg)](https://ada.csse.rose-hulman.edu/kurianj/EAGM/commits/master)

#EAGM
This project is an implementation of a simple web server. [insert other stuff]

The team members include: Isaiah Smith, Lexi Harris, Charlie Horton, and Jon Kurian.

Architecture<br/ >
![alt tag](https://ada.csse.rose-hulman.edu/kurianj/EAGM/raw/master/architecture.png)
In our architecture you can see that we use some services including ....... 

Detailed Design<br/ >
![alt tag](https://ada.csse.rose-hulman.edu/kurianj/EAGM/raw/master/lifecycle.png)
[insert design highlight]


Architectural Evaluation Scenario <br/ >
	Availability<br/ >
		Time to fail: A malicious HTTP client sends an extremely high frequency of requests 
		to the server causing an overloaded operating condition. The measurement is how long 
		it takes the server to go down.<br/ >
			Concrete Test Plan<br/>
				Source: <br/>
				Stimulus: <br/>
				Artifact: <br/>
				Environment: <br/>
				Response: <br/>
				Response Measure:<br/ >
			Test Plan<br/ >
		Time between failure: A malicious HTTP client sends a high frequency of requests to 
		cause the server to be in an overloaded operation condition and go down. This process 
		repeats until we can calculate the mean time between these failures.<br/ >
			Concrete Test Scenario<br/ >
				Source: <br/>
				Stimulus: <br/>
				Artifact: <br/>
				Environment: <br/>
				Response: <br/>
				Response Measure:<br/ >
			Test Plan<br/ >
	Performance<br/ >
		Normal time to serve: HTTP client sends GET requests to the server in normal operating 
		conditions. The measurement is the time from the request until the time that the server 
		sends the 200 OK response.<br/ >
			Concrete Test Scenario<br/ >
				Source: VM running a bash script with curl requests<br/>
				Stimulus: send 20 GET requests to the server one after another<br/>
				Artifact: server process itself<br/>
				Environment: normal operating condition<br/>
				Response: return the file data and send a 200 OK response<br/>
				Response Measure: time to serve<br/ >
			Test Plan<br/ >
		Throughput: HTTP client sends multiple GET requests per minute under normal operating 
		conditions. The measurement is the number of requests that the server responds to per second.<br/ >
			Concrete Test Scenario<br/ >
				Source: VM running a bash script with curl requests<br/>
				Stimulus: send nonstop GET requests for 60 seconds<br/>
				Artifact: server process itself<br/>
				Environment: normal operating condition<br/>
				Response: for each request, return the file data and send 200 OK response<br/>
				Response Measure: requests handled per second<br/ >
			Test Plan<br/ >
	Security<br/ >
		1: <br/ >
			Concrete Test Scenario<br/ >
				Source: Postman requests to server<br/>
				Stimulus: Send in a .html file <br/>
				Artifact: server process itself <br/>
				Environment: normal operating conditions<br/>
				Response: for each request, return the appropriate file and response<br/>
				Response Measure:does the system allow potentially risky files? ex: .html <br/ >
			Test Plan<br/ >
		2: <br/ >
			Concrete Test Scenario<br/ >
				Source: VM running a bash script with curl requests<br/>
				Stimulus: send post and put request with password as header<br/>
				Artifact: server process itself<br/>
				Environment: normal operating condition<br/>
				Response: for request with correct password alter file<br/>
				Response Measure: requests handled correctly<br/ >
			Test Plan<br/ >

Improvement Tactics<br/ >
	Heartbeat<br/ >
	Retry<br/ >
	...<br/ >
	...<br/ >
	...<br/ >
	...<br/ >
	...<br/ >
	...<br/ >

<br/ > DENIAL OF SERVICE SCENARIO  (Availability)<br/ >
A user tries to take down the server by sending multiple get requests. We do not let that happen by monitoring the IP addresses and the time of the requests and then reboot the server according to that information.<br/ >
<br/ >Concrete Test Scenario<br/ >
See denialOfService.sh and ConnectionHandler for details.
<br/ >Source Source of stimulus<br/ >
A User tries to take down the server by sending multiple get requests
Stimulus Input event<br/ >
Environment The condition server is running (e.g. Overloaded: 3 machines making
service requests at 5 request/seconds)<br/ >
<br/ >Artifacts = ConnectionHandler, Server<br/ >
Response The server behavio = The Server reboots itself and monitors further use of that ip address and time of the requestr<br/ >
Reponse Measure Measurement of success/failure of the server behavior<br/ >
Time to Fail<br/ >
<br/ >Test Plan<br/ >
A stepwise testing procedure to measure the scenario = denialOfService.sh<br/ >

Result<br/ >
	[insert description about testing infrastructure]<br/ >
	[insert table]

Future Improvements<br/ >
	[EAGM forever]<br/ >