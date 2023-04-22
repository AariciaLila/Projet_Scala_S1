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
  def apply(s: String): Pixel = s.split(",").map(_.trim).map(_.toInt) match {
  case Array(x, y) => Pixel(x, y, '.')
  case _ => throw new IllegalArgumentException("Invalid input string for Pixel construction")
}

  /**
   * Create a Pixel from a string "x,y" and a color 
   */
  def apply(s: String, color: Char): Pixel =
  s.split(",").map(_.trim).map(_.toInt) match {
    case Array(x, y) => Pixel(x, y, color)
    case _ => throw new IllegalArgumentException("Invalid coordinates")
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
    
    val newPixels = pixels.zipWithIndex.map {
      case (row, y) =>
        if (y == pixel.y) row.updated(pixel.x, pixel) else row
    }

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

  /**
   * Create a static canvas with a default width, height, and character of each pixel
   */
  def new_canvas(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    if (arguments.size < 3) {
      (canvas, Status(error = false, message = "new_canvas action expects 3 arguments"))
    } else if (!arguments(0).forall(_.isDigit) || !arguments(1).forall(_.isDigit)) {
      (canvas, Status(error = true, message = "new_canvas width and height must be integers"))
    } else if (arguments(0).toInt <= 0 || arguments(1).toInt <= 0) {
      (canvas, Status(error = true, message = "new_canvas width and height must be positive integers"))
    } else {
      val width = arguments(0).toInt
      val height = arguments(1).toInt
      val color = arguments(2).head
      val pixels = Vector.tabulate(height, width) { case (row, col) =>
        Pixel(col, row, color)
      }
      val new_canvasCanvas = Canvas(width, height, pixels)
      (new_canvasCanvas, Status())
    }
  }

  /**
   * Updates a pixel on the canvas from its coordinates, changing its colour
   */
  def update_pixel(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    if (arguments.size != 3 && arguments.size != 2) {
      (canvas, Status(error = true, message = "update_pixel action expects 2 or 3 arguments"))
    } else if (!arguments(0).contains(",") || arguments.last.length != 1) {
      (canvas, Status(error = true, message = "update_pixel x and y must be separated by a comma, and color must be a single character"))
    } else {
      val Array(x_str, y_str) = arguments(0).split(",")
      val x = x_str.toInt
      val y = y_str.toInt
      if (x < 1 || y < 1 || x > canvas.width || y > canvas.height) {
        (canvas, Status(error = true, message = "update_pixel position is out of canvas bounds"))
      } else {
        val color = arguments.last.head
        val canvas_update = canvas.update(Pixel(x - 1, y - 1, color))
        (canvas_update, Status())
      }
    }
  }

  /**
   * Draw figures on horizontal and vertical lines
   */
  def draw_line(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    if (arguments.size < 3) {
      (canvas, Status(error = true, message = s"draw_line action expects 3 arguments."))
    } else if (arguments(2).length != 1) {
      (canvas, Status(error = true, message = "draw_line color must be a single character."))
    } else {
      val pixel1 = Pixel(arguments(0))
      val pixel2 = Pixel(arguments(1))
      val color = arguments(2)
      if (pixel1.x >= 1 && pixel1.x <= canvas.width && pixel1.y >= 1 && pixel1.y <= canvas.height &&
        pixel2.x >= 1 && pixel2.x <= canvas.width && pixel2.y >= 1 && pixel2.y <= canvas.height) {
        if (pixel1.x == pixel2.x) {
          val pixels = (pixel1.y to pixel2.y).map(y => Pixel(pixel1.x, y, color.head))
          (canvas.updates(pixels), Status())
        } else if (pixel1.y == pixel2.y) {
          val pixels = (pixel1.x to pixel2.x).map(x => Pixel(x, pixel1.y, color.head))
          (canvas.updates(pixels), Status())
        } else {
          (canvas, Status(error = true, message = "draw_line action only supports horizontal or vertical lines"))
        }
      } else {
        (canvas, Status(error = true, message = "draw_line action only supports pixels within canvas bounds"))
      }
    }
  }

  /**
   * Draw a rectangle in the canvas from the horizontal and vertical lines
   */
  def draw_rectangle(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    if (arguments.size < 3) {
      (canvas, Status(error = true, message = s"draw_rectangle action expects 3 arguments"))
    } else {
      try {
        val pixel1 = Pixel(arguments(0))
        val pixel2 = Pixel(arguments(1))
        val color = arguments(2)
        val top_left = Pixel(pixel1.x.min(pixel2.x), pixel1.y.min(pixel2.y))
        val bottom_right = Pixel(pixel1.x.max(pixel2.x), pixel1.y.max(pixel2.y))
        if (top_left.x < 0 || top_left.y < 0) {
          (canvas, Status(error = true, message = s"draw_rectangle action expects two positive points"))
        } else {
          val top_line = s"${top_left.x},${top_left.y} ${bottom_right.x},${top_left.y} $color"
          val bottom_line = s"${top_left.x},${bottom_right.y} ${bottom_right.x},${bottom_right.y} $color"
          val left_line = s"${top_left.x},${top_left.y+1} ${top_left.x},${bottom_right.y-1} $color"
          val right_line = s"${bottom_right.x},${top_left.y+1} ${bottom_right.x},${bottom_right.y-1} $color"
          val rectanglelines = Seq(top_line, bottom_line, left_line, right_line)
          rectanglelines.foldLeft((canvas, Status())) {
            case ((c, s), line) =>
            val (updatedCanvas, status) = draw_line(line.split(" ").toSeq, c)
            (updatedCanvas, Status())
          }
        }
      } catch {
        case _: IllegalArgumentException =>
        (canvas, Status(error = true, message = "draw_rectangle action expects two pairs of positive integers followed by a character"))
      }
    }
  }

  /**
   * Colours the selected pixel in the canvas and all other pixels of the same colour
   */
  def draw_fill(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    if (arguments.size != 2) {
      return (canvas, Status(error = true, message = s"draw_fill action expects 2 arguments"))
    }
    val pixelResult = try {
      Right(Pixel(arguments(0)))
    } catch {
      case e: Exception => Left(s"The first argument of the draw_fill action must be in x,y format")
    }
    val colorResult = try {
      Right(arguments(1).charAt(0))
    } catch {
      case e: Exception => Left(s"The second argument to the draw_fill action must be a single character")
    }
    (pixelResult, colorResult) match {
      case (Right(pixel), Right(new_color)) =>
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
      val updated_canvas = draw_fill_recursive(pixel.x, pixel.y, canvas)
      val canvas_rows = updated_canvas.pixels.map(_.map(_.color).mkString(""))
      println(canvas_rows.mkString("\n"))
      (updated_canvas, Status())
      case (Left(pixelError), Left(colorError)) =>
        (canvas, Status(error = true, message = s"$pixelError, $colorError"))
      case (Left(pixelError), _) =>
        (canvas, Status(error = true, message = pixelError))
      case (_, Left(colorError)) =>
        (canvas, Status(error = true, message = colorError))
    }
  }

  /**
   * Create a canvas from an existing file
   */
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