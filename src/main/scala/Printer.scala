import java.io.File
import java.awt.print.PrinterJob
import java.awt.print.PrinterException

import javax.print.PrintService
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.PrintRequestAttributeSet
import javax.print.attribute.standard.PageRanges
import javax.print.attribute.standard.Sides
import javax.print.attribute.standard.Copies
import javax.print.attribute.standard.JobName
import javax.print.attribute.standard.Chromaticity

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.PDFPageable

/** Handels all the interactions with the printer */
object Printer {
  
  /** Prints a pdf file to the standard printer with the following settings:
   *  - job name is same as product
   *  - all pages
   *  - double-sided (long edge)
   *  - in color
   *
   *  @param file the pdf file to print
   *  @param numCopies number of printed copies of the file
   */
  def printPDF(file: File, numCopies: Int): Unit = {
    if (!file.getName().endsWith("pdf")) 
      throw new PrinterException(file.getName() + " is not a pdf")

    val pdf = PDDocument.load(file)
    val job = PrinterJob.getPrinterJob()
    job.setPageable(new PDFPageable(pdf))

    val attr = new HashPrintRequestAttributeSet()
    attr.add(new PageRanges(1, pdf.getNumberOfPages()))
    attr.add(Sides.TWO_SIDED_LONG_EDGE)
    attr.add(new Copies(numCopies))
    attr.add(new JobName(file.getName(), null))
    attr.add(Chromaticity.COLOR)

    job.print(attr)

    pdf.close()
  }

  /** Prints one copy of all pdfs in a directory
   *
   *  @param dir the directory with pdfs to print
   */
  def printDirectory(dir: String): Unit = {
    val d = new File(dir) 
    for (file <- d.listFiles if file.getName.endsWith(".pdf")) {
      printPDF(file, 1)
    }
  }

  /** Prints all orders, requires all TDSs to be downoaded
   *
   *  @param dir directory will downlaoded pdf
   *  @param orders mapping between product name and number of copies to print
   */
  def printOrders(dir: String, orders: Map[String, Int]): Unit = {
    println("-----------------------------")
    println("Starting to print")
    for (((id, numCopies), index) <- orders.toSeq.zipWithIndex) {
      println(s"${index+1}/${orders.size}\t$id")
      
      val file = new File(dir + id + ".pdf")
      printPDF(file, numCopies)
    }
  }
}
