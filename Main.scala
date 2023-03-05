import scala.collection.immutable.ArraySeq
import scala.io.Source

/**
 * Main app containg program loop
 */
object Main extends App {

  println("Starting application")

  val status = run()

  println("\nExiting application")
  println(s"Final status: ${status.message}")

  /**
   * Read action from Stdin and execute it
   * Exit if action is 'exit' or if an error occured (status > 0)
   * DO NOT MODIFY THIS FUNCTION
   */
  def run(canvas: Canvas = Canvas()): Status = {
    println("\n======\nCanvas:")
    canvas.display

    print("\nAction: ")

    val action = scala.io.StdIn.readLine()

    val (newCanvas, status) = execute(ArraySeq.unsafeWrapArray(action.split(' ')), canvas)

    if (status.error) {
      println(s"ERROR: ${status.message}")
    }

    if (status.exit) {
      status 
    } else {
      run(newCanvas)  
    }
  }

  /**
   * Execute various actions depending on an action command and optionnaly a Canvas
   */
  def execute(action: Seq[String], canvas: Canvas): (Canvas, Status) = {
    val execution: (Seq[String], Canvas) => (Canvas, Status) = action.head match {
      case "exit" => Canvas.exit
      case "dummy" => Canvas.dummy
      case "dummy2" => Canvas.dummy2
      case "new_canvas" => Canvas.new_canvas
      case "load_image" => Canvas.load_image
      case _ => Canvas.default
    }

    execution(action.tail, canvas)
  }
}

/**
 * Define the status of the previous execution
 */
case class Status(
  exit: Boolean = false,
  error: Boolean = false,
  message: String = ""
)

/**
 * A pixel is defined by its coordinates along with its color as a char
 */
case class Pixel(x: Int, y: Int, color: Char = ' ') {
  override def toString(): String = {
    color.toString
  }
}

/**
 * Companion object of Pixel case class
 */
object Pixel {
  /**
   * Create a Pixel from a string "x,y"
   */
  def apply(s: String): Pixel = {
val coordinates = s.split(",").map(_.toInt)
Pixel(coordinates(0), coordinates(1), '.')
}

  /**
   * Create a Pixel from a string "x,y" and a color 
   */
  def apply(s: String, color: Char): Pixel = {
val coordinates = s.split(",").map(_.toInt)
Pixel(coordinates(0), coordinates(1), color)
}
}

/**
 * A Canvas is defined by its width and height, and a matrix of Pixel
 */
case class Canvas(width: Int = 0, height: Int = 0, pixels: Vector[Vector[Pixel]] = Vector()) {

  /**
   * Print the canvas in the console
   */
  def display: Unit = {
    if (pixels.size == 0) {
      println("Empty Canvas")
    } else {
      println(s"Size: $width x $height")
      for (row <- pixels) {
      println(row.mkString(""))
    }
    }
  }

  /**
   * Takes a pixel in argument and put it in the canvas
   * in the right position with its color
   */
  def update(pixel: Pixel): Canvas = {
    
    val newPixels = pixels // TODO - Update pixels

    this.copy(pixels = newPixels)
  }

  /**
   * Return a Canvas containing all modifications
   */
  def updates(pixels: Seq[Pixel]): Canvas = {
    pixels.foldLeft(this)((f, p) => f.update(p))
  }

  // TODO: Add any useful method
}

/**
 * Companion object for Canvas case class
 */
object Canvas {
  /**
   * Exit execution
   */
  def exit(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = 
    (canvas, Status(exit = true, message="Received exit signal"))

  /**
   * Default execution for unknown action
   */
  def default(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = 
    (canvas, Status(error = true, message = s"Unknown command"))

  /**
   * Create a static Canvas
   */
  def dummy(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = 
    if (arguments.size > 0) 
      (canvas, Status(error = true, message = "dummy action does not excpect arguments"))
    else  {
      val dummyCanvas = Canvas(
        width = 3,
        height = 4,
        pixels = Vector(
          Vector(Pixel(0, 0, '#'), Pixel(1, 0, '.'), Pixel(2, 0, '#')),
          Vector(Pixel(0, 1, '#'), Pixel(1, 1, '.'), Pixel(2, 1, '#')),
          Vector(Pixel(0, 2, '#'), Pixel(1, 2, '.'), Pixel(2, 2, '#')),
          Vector(Pixel(0, 3, '#'), Pixel(1, 3, '.'), Pixel(2, 3, '#'))
        )
      )
      
      (dummyCanvas, Status())
    }

  /**
   * Create a static canvas using the Pixel companion object
   */
  def dummy2(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = 
    if (arguments.size > 0) 
      (canvas, Status(error = true, message = "dummy action does not excpect arguments"))
    else  {
      val dummyCanvas = Canvas(
        width = 3,
        height = 1,
        pixels = Vector(
          Vector(Pixel("0,0", '#'), Pixel("1,0"), Pixel("2,0", '#')),
        )
      )
      
      (dummyCanvas, Status())
    }

  def new_canvas(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    if (arguments.size < 3) {
      (canvas, Status(error = false, message = "new_canvas action does not have enough arguments"))
    } else {
      val new_canvasCanvas = Canvas(
        width = arguments(0).toInt,
        height = arguments(1).toInt,
        pixels = Vector.fill(arguments(1).toInt)(Vector.fill(arguments(0).toInt)(Pixel(0, 0, arguments(2).head)))
      )

      (new_canvasCanvas, Status())
    }
  }

  def load_image(arguments: Seq[String], canvas: Canvas): (Canvas, Status) =
    if (arguments.size < 1)
      (canvas, Status(error = true, message = "load_image action expects one argument"))
    else {
      val filename = arguments(0)
      try {
        val content: Vector[String] = Source.fromFile(filename).getLines().toVector
        val pixels = content.map { line =>
          line.map(char => Pixel(0, 0, char)).toVector
        }
        val newCanvas = Canvas(pixels(0).size, pixels.size, pixels)
        // Print canvas
        newCanvas.pixels.foreach { row =>
          row.foreach { pixel =>
            print(pixel.color)
          }
          println()
        }
        (newCanvas, Status())
      } catch {
        case e: Exception => (canvas, Status(error = true, message = s"Image loading error: $e."))
      }
    }

}
