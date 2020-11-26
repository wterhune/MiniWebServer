import java.io.*;
import java.net.*;

/*
1. Explained how MIME-types are used to tell the browser what data is coming.

    Answer:
    Multipurpose Internet Mail Extensions, known as MIME, are often used to tell the
    browser what data is coming. Specifically, for HTTP requests, MIME allows the web browser
    to communicate with the server and they contain different fields
    in the header for every communication.
    These fields can include, for example, "GET /WebAdd.fake-cgi?person=Wisa&num1=2&num2=4 HTTP/1.1".

    Essentially, MIME will look at WebAdd.html and realizes that WebAdd.html wants to accept content type of "html".
    So, MIME will return the data of that type.

2. Explained how you would return the contents of requested files (web pages)
of type HTML (text/html).

    Answer:
    This assignment returns the web response in content type of HTML. We query the string that MIME gives us and we
    extract certain fields that we are interested in. The, we send a response in HTML format using tags.


3. Explained how you would return the contents of requested files (web pages)
of type TEXT (text/plain)

    Answer:
    The contents of the requested files (web pages) would return as type TEXT when
    the content type is set to "text/plain". In this assignment, we can have content type set to "text/plain" and
    just return the extracted string fields of name, num1, num2, and total to the response without HTML tags.
 */

public class MiniWebServer {

    //The below code is provided by CS 435 with port number modification
    public static void main(String a[]) throws IOException {

        int queueLength = 6; //maximum length of requests at a time and can be changed
        int portNumber = 2540; //direction says port is defaulted to 2540

        Socket socket;

        //creating server communication with port 2540
        ServerSocket serverSock = new ServerSocket(portNumber, queueLength);

        System.out.println("Wisa Terhune-Praphruettam's Mini Web Server Starting on Port ==> " + portNumber);

        while (true) {
            //start server communication
            socket = serverSock.accept();
            new Worker(socket).start();
        }
    }
}

/*
This code has been modified from CS 435 Worker class for server communication.
The input will read a message from WebAdd.html and see if it will return a web response.
If the incoming input has "favicon", then it will not run add number method.
 */
class Worker extends Thread {

    Socket socket;

    Worker(Socket s) {
        socket = s;
    } //worker constructor

    public void run() {
        // I/O set up
        PrintStream output; //setting up output
        BufferedReader input; //setting up input reading

        try {
            output = new PrintStream(socket.getOutputStream()); //output communication
            input = new BufferedReader(new InputStreamReader(socket.getInputStream())); //input reading

//            *** not sure if need the code here ***
//            System.out.println("Sending the HTML Reponse now: " +
//                    Integer.toString(WebResponse.i) + "\n" );

            //This will set up the incoming information GET request from WebAdd.html
            //We will put this into the String request to parse later
            String request;
            request = input.readLine(); //reading GET request from WebAdd.html

            System.out.println("I have received a GET request from WebAdd.html..... \n");
            System.out.println("The request is ===> \n" + request); //for debugging purposes

            //will not run the add number method with the request since it has favicon
            if(request.contains("/favicon")) {
                output.println("This is a favicon request.");
            }
            else {
                System.out.print("Please wait shortly for a web response.\n");
                addNumber(output, request);
            }

            socket.close();
            //clears data
            System.out.flush();
        } catch (IOException e) {
            //for debugging purposes
            System.out.println("Sorry, Mini Web Server is unable to connect.");
            e.printStackTrace();
            e.getMessage();
            e.getCause();
        }
    }

    /*
    This addNumber method will take the input request from WebAdd.html and splits the string into an array.
    The extracted string will be added and the response will be sent back.
     */

    public void addNumber(PrintStream output, String request) {
        //here we are just extracting the string we want which are person, num1, and num2 fields:
        //GET /WebAdd.fake-cgi?person=Wisa&num1=2&num2=4 HTTP/1.1
        //person=YourName&num1=2&num2=4 HTTP/1.1 as an example

        //We are trimming the incoming request string from the ending so that it ends after num2 value
        int ending = request.length() - 9;
        //System.out.println("After trimming the ending of request " + request.substring(ending)); //for debuggin purposes

        //This will get rid of the characters before "YourName" and will stop at "=4"
        String queryString = request.substring(28, ending); //up to 21 characters
        System.out.println("After trimming the beginning of request to the end of string " + queryString);

        //The current string should look like YourName&num1=2&num2=4
        //We will put this into an array so that we can select individual field for computation
        String[] splitQuery = queryString.split("[=&]");

        //[YourName, num1, 2, num2, 4] should be what is left in the array after the split
        //the line below is for name
        String name = splitQuery[0];
        System.out.printf("The entered name is %s", name);

        //the line below is for the first number
        String num1 = splitQuery[2];
        System.out.printf("The first entered number is %s", num1);

        //the line below is for the second number
        String num2 = splitQuery[4];
        System.out.printf("The second number entered is %s", num2);

        //I wasn't sure if you could enter a Double type so this is just in case someone enters decimal digits
        double add = Double.parseDouble(num1) + Double.parseDouble(num2);
        String response = "Hello, " + name + "! You have entered the numbers "
                + num1 + " and " + num2 + ", respectively. Your total is " + add + ".";

        //We need to generate a response back to the user.
        String HTMLResponse = "<html> <h1> Hello Browser World! </h1>";
        output.println("HTTP/1.1 200 OK");
        output.println("Content Length: " + response.length()); //length of content
        output.println("Content-type: text/html \r\n\r\n");
        output.println(HTMLResponse + "<p>" + response + "</p>"); //response

        //This is to help refresh the WebAdd page. The HTML format is from the WebAdd.html view source
        output.print("<HTML>");
        output.print("<HEAD>");
        output.print("<link rel=\"icon\" href=\"data:,\">");
        output.print("</HEAD>");
        output.print("<BODY>");
        output.print("<H1> WebAdd </H1>");
        output.print("<FORM method=\"GET\" action=\"http://localhost:2540/WebAdd.fake-cgi\">");
        output.print("<INPUT TYPE=\"text\" NAME=\"person\" size=20 value=\"YourName\"><P>");
        output.print("<INPUT TYPE=\"text\" NAME=\"num1\" size=5 value=\"4\"> <br>");
        output.print("<INPUT TYPE=\"text\" NAME=\"num2\" size=5 value=\"5\"> <p>");
        output.print("<INPUT TYPE=\"submit\" VALUE=\"Submit Numbers\">");
        output.print("</FORM>");
        output.print("</BODY>");
        output.print("</HTML");
        output.println("</html>");
    }
}

