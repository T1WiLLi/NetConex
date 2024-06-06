# NetConex

NetConex is a Java library that provides easy-to-use functionality for making HTTP requests, handling errors, and configuring timeouts and retries.

## Features

- Supports GET, POST, PUT, and DELETE HTTP methods.
- Asynchronous request execution.
- Customizable timeout configuration.
- Retry mechanism for failed requests.
- Error handling improvements.

## Installation

I'm currently working on that, but it will soon be available on Maven Central :)
In the meantime, you can use the .Jar file (it's a modular library) so you will need to include a module-info.java

## Usage 

<p>
  NetConex gives you access to four request methods, let's explore them!
</p>

#### Universal Request 

You can do a universal request by using only the NetConex class : 
```java
package qc.netconex;

public class Main {
    public static void main(String[] args) {
        NetConex netConex = new NetConex("www.example.com");
        netConex.executeAsync("/api", "GET", null);
    }
}
```

Note that if you are using the NetConex, you can only do Async request, this is to avoid settings up a queue for multiple request using the same object which could lead to the removal of previous requests.

#### GET Request

The request you will probably do the most... 

```java
package qc.netconex;

import qc.netconex.error.ApiRequestException;
import qc.netconex.request.Get;

public class Main {
    public static void main(String[] args) {
        NetConex netConex = new NetConex("www.example.com");

        Get getRequest = new Get(netConex);
        try {
            String jsonResponse = getRequest.execute("/api");
            String formattedResponse = getRequest.formatResponse(jsonResponse);
        } catch (ApiRequestException e) {
            e.printStackTrace();
        }
    }
}
```

As you can see, the Get request is a bit more complicated, in order, you will retrieve the ```jsonResponse``` which is the raw data from the API, then using the ```formatResponse``` method you will get a more understandable json String. In this case it is not the most intersting things. You might want to deserialize the json directly into an object (A class) that you previously built for that purpose. Well good news! You can!

```java 
package qc.netconex;

import qc.netconex.error.ApiRequestException;
import qc.netconex.request.Get;

public class Main {

    public class InnerMain {
        /* Some data */
    }

    public static void main(String[] args) {
        NetConex netConex = new NetConex("www.example.com");

        Get getRequest = new Get(netConex);
        try {
            InnerMain innerMain = getRequest.executeAndDeserialize("/api/things", InnerMain.class);
        } catch (ApiRequestException e) {
            e.printStackTrace();
        }
    }
}
```


Here, we specify the object we want to populate, be carefull as the Class passed must exactly match the return JSON object.


#### POST Request : 

The next request on our list is the POST request one of the few used on the net.

```java 
package qc.netconex;

import java.util.HashMap;

import qc.netconex.error.ApiRequestException;
import qc.netconex.request.Post;

public class Main {

    public static void main(String[] args) {
        NetConex netConex = new NetConex("www.example.com");

        Post postRequest = new Post(netConex);
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("key", "value");
        try {
            postRequest.execute("/api", requestBody);
        } catch (ApiRequestException e) {
            e.printStackTrace();
        }
    }
}
```
This is the most basic way to build the request body, you need to instantiate a Map and then populate it with the key-value in order to build the requestBody. This is a bit tedious so you could also pass an object directly. Be carefull, again the object format and variables must match the expected from the API you're making the call to. 

But... I decided to also add some more ways to do it.

```java
package qc.netconex;

import qc.netconex.request.Post;

public class Main {

    public static void main(String[] args) {
        NetConex netConex = new NetConex("www.example.com");

        Post postRequest = new Post(netConex);
        String[] requestBodyItem = new String[0];
        try {
            postRequest.execute("/api", postRequest.buildRequestBodyFromArray(requestBodyItem, Main.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

In this case, you will be building a requestBody from a very basic Stirng array, the class specified is the blueprint for the JSON body, which mean that if you have an array let's say : 

```java
{"name", "lastName"}

// Class that you pass : 

public class Thing {
  private String name;
  private String lastName;
}
```

This is all great and all, but sometimes you might want to take a look at the body before sending it, maybe you have pre-processing to do, welp, I've thinkered about it and went for a small detour : 
```
   public Map<String, Object> buildRequestBodyFromObject(Object object) throws ApiRequestException {
        try {
            return objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new ApiRequestException("Error building request body from object", e);
        }
    }
```

This function takes an object of you're choice and transform it into a Map<String, Object>, that way you can then see those key-values and make sure everyythings works, it's also great for logging the data themselves.


We basically just map the values of the array to the key of the object.


## Async function !

Quick word on them, they do work, they are complicated to work with, once you understand them, you will have a great power. 

```java 
package qc.netconex;

import java.util.concurrent.CompletableFuture;

import qc.netconex.error.JsonFormattingException;
import qc.netconex.error.JsonParsingException;
import qc.netconex.request.Get;

public class Main {

    public static void main(String[] args) {
        NetConex netConex = new NetConex("https://dummyjson.com");

        Get getRequest = new Get(netConex);
        CompletableFuture<String> formattedJson = getRequest.executeAsync("/users", null).thenApply(res -> {
            try {
                return getRequest.formatResponse(res);
            } catch (JsonParsingException | JsonFormattingException e) {
                e.printStackTrace();
            }
            return null;
        });
        System.out.println(formattedJson.join()); // Join function waits for the request to finish then proceed
    }
}

```

You could also directly process the data : 

```java
package qc.netconex;

import qc.netconex.error.JsonFormattingException;
import qc.netconex.error.JsonParsingException;
import qc.netconex.request.Get;

public class Main {

    public static void main(String[] args) {
        NetConex netConex = new NetConex("https://dummyjson.com");

        Get getRequest = new Get(netConex);
        getRequest.executeAsync("/users", null).thenApply(res -> {
            try {
                return getRequest.formatResponse(res);
            } catch (JsonParsingException | JsonFormattingException e) {
                e.printStackTrace();
            }
            return null;
        }).thenAccept(formatResponse -> {
            System.out.println(formatResponse);
        });
    }
}
```

Obviously here, we are 100% using lamba expression, which you should learn about if you still don't use them ! The pros of doing things this way is that you can start processing right away.

As for the rest of the request they are all pretty much the same as those two, also you must take into account that the requestBody when calling ```executeAsync``` from the NetConex class can be ```NULL```.


