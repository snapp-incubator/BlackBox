package cab.snapp.blackBox.poaro

import java.io.File

interface ReportGenerator {
     fun createAndGetReport(timeRange: Int, outputFileName: String) : File
}