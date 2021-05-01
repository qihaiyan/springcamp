package scripts

class Greeter {
    String sayHello(arg, funBean) {
        def greet = new Dependency().message + ", with arg: " + arg + " funBean: " + funBean.funName
        greet
    }
}

Greeter greeter = new Greeter()
greeter.sayHello(arg, funBean)

int calcSum(int x, int y) { return x + y }