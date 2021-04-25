package scripts

class Greeter {
    String sayHello(arg) {
        def greet = new Dependency().message + ", with arg: " + arg
        greet
    }
}

Greeter greeter = new Greeter()
greeter.sayHello(arg)

int calcSum(int x, int y) { return x + y }