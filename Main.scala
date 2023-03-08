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
      case "update_pixel" => Canvas.update_pixel
      case "draw_line" => Canvas.draw_line
      case "draw_rectangle" => Canvas.draw_rectangle
      case "draw_fill" => Canvas.draw_fill
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
    
    val newPixels = pixels.updated(pixel.y, pixels(pixel.y).updated(pixel.x, pixel))

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
      (canvas, Status(error = false, message = "new_canvas action expects 3 arguments"))
    } else {
      val new_canvasCanvas = Canvas(
        width = arguments(0).toInt,
        height = arguments(1).toInt,
        pixels = Vector.fill(arguments(1).toInt)(Vector.fill(arguments(0).toInt)(Pixel(0, 0, arguments(2).head)))
      )

      (new_canvasCanvas, Status())
    }
  }

  def update_pixel(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    if (arguments.size < 3) {
      (canvas, Status(error = true, message = s"update_pixel action expects 3 arguments"))
    } else {
      val x = arguments(0).toInt
      val y = arguments(1).toInt
      val color = arguments(2).head
      val canvas_update = canvas.update(Pixel(x, y, color))
      
      val canvas_rows = canvas_update.pixels.map(_.map(_.color).mkString(""))
      println(canvas_rows.mkString("\n"))
      (canvas_update, Status())
    }
  }

  def draw_line(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
  if (arguments.size < 3) {
    (canvas, Status(error = true, message = s"draw_line action expects 3 arguments."))
  } else {
    val pixel1 = Pixel(arguments(0))
    val pixel2 = Pixel(arguments(1))
    val color = arguments(2)

    if (pixel1.x == pixel2.x) {
      // vertical line
      val pixels = (pixel1.y to pixel2.y).map(y => Pixel(pixel1.x, y, color.head))
      (canvas.updates(pixels), Status())
    } else if (pixel1.y == pixel2.y) {
      // horizontal line
      val pixels = (pixel1.x to pixel2.x).map(x => Pixel(x, pixel1.y, color.head))
      (canvas.updates(pixels), Status())
    } else {
      // neither horizontal nor vertical line
      (canvas, Status(error = true, message = "draw_line action only supports horizontal or vertical lines"))
    }
  }
}

  def draw_rectangle(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
  if (arguments.size < 3) {
    (canvas, Status(error = true, message = s"draw_rectangle action expects 3 arguments"))
  } else {
    val pixel1 = Pixel(arguments(0))
    val pixel2 = Pixel(arguments(1))
    val color = arguments(2)

    val topLeftCorner = Pixel(pixel1.x.min(pixel2.x), pixel1.y.min(pixel2.y))
    val bottomRightCorner = Pixel(pixel1.x.max(pixel2.x), pixel1.y.max(pixel2.y))

    val topLine = s"${topLeftCorner.x},${topLeftCorner.y} ${bottomRightCorner.x},${topLeftCorner.y} $color"
    val bottomLine = s"${topLeftCorner.x},${bottomRightCorner.y} ${bottomRightCorner.x},${bottomRightCorner.y} $color"
    val leftLine = s"${topLeftCorner.x},${topLeftCorner.y+1} ${topLeftCorner.x},${bottomRightCorner.y-1} $color"
    val rightLine = s"${bottomRightCorner.x},${topLeftCorner.y+1} ${bottomRightCorner.x},${bottomRightCorner.y-1} $color"

    val rectanglelines = Seq(topLine, bottomLine, leftLine, rightLine)

    rectanglelines.foldLeft((canvas, Status())) { case ((c, s), line) =>
      val (updatedCanvas, status) = draw_line(line.split(" ").toSeq, c)
      (updatedCanvas, Status())
    }
  }
  }

  def draw_fill(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
  if (arguments.size > 2) {
    return (canvas, Status(error = true, message = s"draw_fill action expects 2 arguments"))
  }

  val pixel = Pixel(arguments(0))
  val new_color = arguments(1).charAt(0)
  val adjoining_colour = canvas.pixels(pixel.y)(pixel.x).color

  def draw_fill_recursive(x: Int, y: Int, canvas: Canvas): Canvas = {
  if (x < 0 || x >= canvas.width || y < 0 || y >= canvas.height || canvas.pixels(y)(x).color != adjoining_colour) {
    canvas
  } else {
    val updated_canvas = canvas.update(Pixel(x, y, new_color))
    val updated_right = draw_fill_recursive(x + 1, y, updated_canvas)
    val updated_left = draw_fill_recursive(x - 1, y, updated_right)
    val updated_down = draw_fill_recursive(x, y + 1, updated_left)
    val updated_up = draw_fill_recursive(x, y - 1, updated_down)
    updated_up
  }
}


  val update_canvas = draw_fill_recursive(pixel.x, pixel.y, canvas)

  val canvas_rows = update_canvas.pixels.map(_.map(_.color).mkString(""))
  println(canvas_rows.mkString("\n"))

  (update_canvas, Status())
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
        val canvas_image = Canvas(pixels(0).size, pixels.size, pixels)
        val canvas_rows = canvas_image.pixels.map(_.map(_.color).mkString(""))
        println(canvas_rows.mkString("\n"))
        (canvas_image, Status())
      } catch {
        case e: Exception => (canvas, Status(error = true, message = s"Image loading error: $e."))
      }

      
    }

    

    


}
