# <img src="images/logo.png" width="50%" alt="Polylith" id="logo">
An open source tool used to develop Polylith based architectures in Clojure.

---------

Welcome to the wonderful world of Polylith!

This tool is made by developers for developers with the goal to maximise productivity and
increase the quality of the systems we write.
It supports your build pipeline, but is not a build tool itself.

The Polylith concept can be implemented in any programming language,
but this version of the Polylith tool targets [Clojure](https://clojure.org)
which is a powerful and simple functional language for the [JVM](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwiB88eLxansAhUyi8MKHd6jDPEQFjAAegQIBRAC&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FJava_virtual_machine&usg=AOvVaw0YtnMyoG7GQIhUPeLulbfr).

Polylith introduces the architectural concept of “service level building blocks”,
which can be combined like LEGO bricks to build our services and systems.
Polylith’s LEGO-like bricks are easy to reason about, test, refactor, and reuse.
They allow us to work with all our code in one place for maximum productivity, using a single
[REPL](https://en.wikipedia.org/wiki/Read%E2%80%93eval%E2%80%93print_loop)

The bricks can easily be put together to form different kinds of deployable artifacts,
like services, tools and libraries, in the same way we put together LEGO when we were kids!
Not surprisingly, it's just as simple and fun!

To give you an idea of what that can look like, take a quick look at the bricks and libraries
that we use to build the Polylith tool (which is itself a Polylith workspace, represented by the `poly` column in the first diagram):

<img src="images/polylith-info-deps-libs.png" width="100%">

To better understand the principles and ideas behind this tool, we recommend you first read the...

<p>
<a href="https://polylith.gitbook.io">
<img src="images/doc.png" width="10%" href="https://polylith.gitbook.io">
<br>...high-level documentation.
</a>
</p>

<br>
...before you continue with the...
<p>
<a href="https://polylith.gitbook.io/poly">
<img src="images/doc.png" width="10%" href="https://polylith.gitbook.io/poly">
<br>...poly tool documentation!
</a>
</p>

<br>Enjoy the ride!

## Contact

Feel free to contact me:<br>
&nbsp;&nbsp;Twitter: @jtengstrand<br>
&nbsp;&nbsp;Email: info[at]polyfy[dot]se

You can also get in touch with us on [Slack](https://clojurians.slack.com/archives/C013B7MQHJQ).

## License

Distributed under the [Eclipse Public License 1.0](http://opensource.org/licenses/eclipse-1.0.php), the same as Clojure.
