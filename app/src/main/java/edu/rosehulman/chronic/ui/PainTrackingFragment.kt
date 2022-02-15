package edu.rosehulman.chronic.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.utilities.Constants
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.R.color
import edu.rosehulman.chronic.R.drawable
import edu.rosehulman.chronic.databinding.FragmentPaintrackingBinding
import edu.rosehulman.chronic.models.PainData
import edu.rosehulman.chronic.models.PainDataViewModel
import edu.rosehulman.chronic.models.Tag
import edu.rosehulman.chronic.models.UserData


class PainTrackingFragment : Fragment() {

    private lateinit var binding: FragmentPaintrackingBinding
    private lateinit var model: PainDataViewModel

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart

    private var AllUserTagsByID = HashMap<String, Int>()
    private var AllTagsList  = ArrayList<Tag>()

    private var Subscriptions = ArrayList<ListenerRegistration>()

    //Display 31 or 7
    var displayLastWeek = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model = ViewModelProvider(requireActivity()).get(PainDataViewModel::class.java)
        binding = FragmentPaintrackingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setHasOptionsMenu(true)


        setupButtons()
        readUserFromFireStore()
        readDataModelFromFireStoreUpdate(){
            setupAverageValue()
            getAllTagsFromFireStore {
                AllUserTagsByID = model.getTagsAndAmounts()
                drawBarChart()
                drawPieChart()
            }
        }

        return root
    }

    private fun readDataModelFromFireStoreUpdate(observer: () -> Unit) {
        model.addListener(fragmentName, Firebase.auth.uid!!, observer)
    }

    private fun setupAverageValue() {

        if(model.size() != 0){
            if(model.getAveragePain() < 5){
                binding.averageText.text = "Doing Well"
                binding.averageIcon.load(resources.getDrawable( drawable.ic_baseline_keyboard_arrow_up_24))
                binding.averageText.setTextColor(resources.getColor(color.green))
                binding.averageIcon.setBackgroundColor(resources.getColor(color.green))
            }else{
                binding.averageText.text = "Doing Poorly"
                binding.averageIcon.load(resources.getDrawable( drawable.ic_baseline_keyboard_arrow_down_24))
                binding.averageText.setTextColor(resources.getColor(color.red))
                binding.averageIcon.setBackgroundColor(resources.getColor(color.red))
            }
            binding.painTrackingAverage.text = model.getAveragePain().toString()
        }
    }


    fun setupButtons(){
        binding.ViewMoreDetailsButton.setOnClickListener(){
            findNavController().popBackStack()
            findNavController().navigate(R.id.nav_data)
        }
    }

    fun readUserFromFireStore() {
        var user = UserData()
        Firebase.firestore.collection(UserData.COLLECTION_PATH).document(Firebase.auth.uid!!).get().addOnSuccessListener { snapshot: DocumentSnapshot ->
            user = snapshot.toObject(UserData::class.java)!!
            binding.profileName.text = "Hello ${user.userName}!"

        }


    }

    var ref = Firebase.firestore.collection(Tag.COLLECTION_PATH)
    fun getAllTagsFromFireStore(observer: () -> Unit) {
        val subscription = Firebase.firestore.collection(Tag.COLLECTION_PATH)
            .addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                error?.let {
                    Log.d(Constants.TAG, "Error $error")
                    return@addSnapshotListener
                }
                Log.d(Constants.TAG, "In snapshot listener with ${snapshot?.size()} docs")
                AllTagsList.clear()
                snapshot?.documents?.forEach {
                    val tag = Tag.from(it)
                    tag.isTracked = false
                    AllTagsList.add(tag)
                }
        observer()
    }

        Subscriptions.add(subscription)
    }

    fun convertTagID2Name(tagID:String):String{
        var TagName = ""

        AllTagsList.forEach(){
            if(it.id == tagID){
                TagName = it.title
            }
        }
        return TagName
    }

    private fun drawBarChart(){
        //Create the graph
        barChart = binding.PainTrackingDataGraph
        barChart.animateY(1400, Easing.EaseInOutQuad)


        //Setup Chart data
        //Setup Data on Chart
        //Grab the latest 7 data entries if in week mode, grab the latest 31 entries if in month mode

        val dataList = ArrayList<PainData>()
        if(displayLastWeek){
            // grab the last seven entries (if they exist)
            dataList.addAll(model.getSpecifedDataPoints(7))
        }else{
            dataList.addAll(model.getSpecifedDataPoints(31))
        }

        val values = ArrayList<BarEntry>()
        val colors = ArrayList<Int>()
        val xAxisLabel = ArrayList<String>()
        val green = Color.rgb(110, 190, 102)
        val red = Color.rgb(211, 74, 88)


        for (i in dataList.indices) {
            //Flip to be negative and red for bad entries, and green and positive for good entries
            var dataValue = dataList[i].painLevel.toFloat()
            if(dataValue >= 5){
                dataValue = - dataList[i].painLevel.toFloat()
            }

            values.add(BarEntry((i).toFloat(), dataValue))
            // specific colors, less than 5 is red, otherwise it is green
            if (dataList[i].painLevel >= 5) colors.add(red) else colors.add(green)

            //Only display X axis labels if there is enough space (or are small enough)
            //Handle adding the proper date labels
                xAxisLabel.add(dataList[i].getFormattedStartTime())

        }


        //Set Colors
        barChart.setBackgroundColor(Color.WHITE)
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)

        //Set Graph Padding
        barChart.extraTopOffset = -30f
        barChart.extraBottomOffset = 10f
        barChart.extraLeftOffset = 70f
        barChart.extraRightOffset = 70f

        //Setup the chart description based on which type of data to record
        barChart.description.isEnabled = false
       if(displayLastWeek){
           barChart.description.text = "Weekly Data"
       }else{
           barChart.description.text = "Monthly Data"
       }

        //Can't scale chart in any direction
        barChart.setPinchZoom(false)
        //Don't draw grids
        barChart.setDrawGridBackground(false)




        //Initialize the X axis formatting
        val xAxis = barChart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textColor = Color.BLACK

        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 1f
        xAxis.valueFormatter = (MyAxisValueFormatter(xAxisLabel))
        xAxis.setCenterAxisLabels(false)


        if(displayLastWeek){
            xAxis.labelCount = 7
            xAxis.textSize = 13f
        }else{
            xAxis.labelCount = 31
            xAxis.textSize = 5f
        }

        //Setup the axis itself and the formatting
        val left = barChart.axisLeft
        left.setDrawLabels(false)
        left.spaceTop = 25f
        left.spaceBottom = 25f
        left.setDrawAxisLine(false)
        left.setDrawGridLines(false)
        left.setDrawZeroLine(true) // draw a zero line

        //Setup the zero line color and format
        left.zeroLineColor = resources.getColor(color.plum)
        left.zeroLineWidth = 1f
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = false




        //Take the data, and add it to the chart
        val set: BarDataSet
        if (barChart.data != null &&
            barChart.data.dataSetCount > 0
        ) {
            set = barChart.data.getDataSetByIndex(0) as BarDataSet
            set.values = values
            barChart.data.notifyDataChanged()
            barChart.notifyDataSetChanged()
        } else {
            set = BarDataSet(values, "Values")
            set.colors = colors
            set.setValueTextColors(colors)
            val data = BarData(set)




            //Enable or disable bar values
            if(displayLastWeek){
                data.setValueTextSize(13f)
                data.barWidth = 0.8f
                data.setValueFormatter(MyDataValueFormatter())
                data.setDrawValues(true)
            }else{
                data.setValueTextSize(5f)
                data.barWidth = 0.8f
                data.setValueFormatter(MyDataValueFormatter())
                data.setDrawValues(true)
            }



            barChart.data = data
            barChart.invalidate()
        }
    }

    private fun drawPieChart(){
        //Create the graph
        pieChart = binding.PainTrackingPieChart

        //Setup Chart data
        //Setup Data on Chart
        pieChart.setBackgroundColor(Color.WHITE)
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setCenterText(generateCenterSpannableText())
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)
        pieChart.setDrawCenterText(true)
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)
        pieChart.setMaxAngle(180f) // HALF CHART
        pieChart.setRotationAngle(180f)
        pieChart.setCenterTextOffset(0f, -30f)


        //Load in the Data
        val values = java.util.ArrayList<PieEntry>()

        AllUserTagsByID.forEach(){
            //Data, and then label
            Log.d(Constants.TAG,"Current Tag ${convertTagID2Name(it.key)} has ${it.value} instances")
            values.add(PieEntry(it.value.toFloat(), convertTagID2Name(it.key)))
        }

        //Remove any with no tag names, aka the data for the tag has been removed
        val valuesToRemove = java.util.ArrayList<PieEntry>()
        values.forEach(){
            if(it.label ==""){
                valuesToRemove.add(it)
            }
        }
        //Remove all
        values.removeAll(valuesToRemove)


        val dataSet = PieDataSet(values,"Legend")

        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f


        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)



        //Animation and Legend Setup
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.spin(2000, 0F, 180F,Easing.EaseInOutCirc)
        val pieLegend: Legend = pieChart.legend
        pieLegend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        pieLegend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        pieLegend.orientation = Legend.LegendOrientation.HORIZONTAL
        pieLegend.isWordWrapEnabled = true
        pieLegend.setDrawInside(false)
        pieLegend.xEntrySpace = 7f
        pieLegend.yEntrySpace = 0f
        pieLegend.yOffset = 50f
        pieLegend.isWordWrapEnabled = true

       // entry label styling
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(8f)
        pieChart.setDrawEntryLabels(true)
        pieChart.setUsePercentValues(true)


        //Define all the possible colors for the objects in the pie chart
        // add a lot of colors
        val colors = java.util.ArrayList<Int>()

        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)

        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)

        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)

        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)

        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)

        colors.add(ColorTemplate.getHoloBlue())

        dataSet.colors = colors

        pieChart.setData(data)
        pieChart.invalidate()

    }


    class MyAxisValueFormatter(private val xValsDateLabel: ArrayList<String>) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return value.toString()
        }

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            if (value.toInt() >= 0 && value.toInt() <= xValsDateLabel.size - 1) {
                return xValsDateLabel[value.toInt()]
            } else {
                return ("").toString()
            }
        }
    }

    class MyDataValueFormatter() : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return kotlin.math.abs(value).toString()
        }


    }

    private fun generateCenterSpannableText(): SpannableString {
        val s = SpannableString("Chronic\ndeveloped by Nicholas Snow and Natalie Koenig")
        s.setSpan(RelativeSizeSpan(1.7f), 0, 7, 0)
        s.setSpan(ForegroundColorSpan(resources.getColor(color.plum)), 0, 7, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), 7, 17, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 7, 17, 0)
        s.setSpan(RelativeSizeSpan(.9f), 17, s.length , 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), 17, s.length, 0)
        s.setSpan(ForegroundColorSpan(resources.getColor(color.grape)), 17, s.length, 0)
        return s
    }

    override fun onCreateOptionsMenu(menu: Menu, inflator: MenuInflater) {
        inflator.inflate(R.menu.main_filter, menu)
        super.onCreateOptionsMenu(menu, inflator)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                // User chose the "Settings" item, show the app settings UI...
                true
            }

            R.id.action_filter_toggle -> {
                displayLastWeek = displayLastWeek != true
                barChart.notifyDataSetChanged()
                barChart.invalidate()
                barChart.clearValues()
                barChart.clear()



                //Redraw Chart
                drawBarChart()
                true
            }
            else -> {
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        model.removeListener(fragmentName)
        Subscriptions.forEach(){
            it.remove()
        }
        Subscriptions.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        model.removeListener(fragmentName)
        Subscriptions.forEach(){
            it.remove()
        }
        Subscriptions.clear()
    }

    companion object{
        const val fragmentName = "PainTrackingFragment"
    }
}