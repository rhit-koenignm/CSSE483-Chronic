package edu.rosehulman.chronic.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.databinding.FragmentPaintrackingBinding
import edu.rosehulman.chronic.models.PainData
import edu.rosehulman.chronic.models.PainDataViewModel
import edu.rosehulman.chronic.models.UserData
import java.text.DecimalFormat


class PainTrackingFragment : Fragment() {

    private lateinit var binding: FragmentPaintrackingBinding
    private lateinit var model: PainDataViewModel

    private lateinit var chart: BarChart

    var displayLastWeek = false;
    var displayLastMonth = true;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model = ViewModelProvider(requireActivity()).get(PainDataViewModel::class.java)
        binding = FragmentPaintrackingBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //TODO Clear backstack so you can't get to the loading screen


        setupButtons()
        readUserFromFireStore()
        readDataModelFromFireStoreUpdate(){
            setupAverageValue()
            drawChart()
        }

        return root
    }

    private fun readDataModelFromFireStoreUpdate(observer: () -> Unit) {
        model.addListener(fragmentName, Firebase.auth.uid!!, observer)
    }

    private fun setupAverageValue() {

        if(model.size() != 0){
            if(model.getAveragePain() >= 5){
                binding.averageText.text = "Doing Well"
                binding.averageIcon.load(resources.getDrawable( R.drawable.ic_baseline_keyboard_arrow_up_24))
                binding.averageText.setTextColor(resources.getColor(R.color.green))
                binding.averageIcon.setBackgroundColor(resources.getColor(R.color.green))
            }else{
                binding.averageText.text = "Doing Poorly"
                binding.averageIcon.load(resources.getDrawable( R.drawable.ic_baseline_keyboard_arrow_down_24))
                binding.averageText.setTextColor(resources.getColor(R.color.red))
                binding.averageIcon.setBackgroundColor(resources.getColor(R.color.red))
            }
            binding.painTrackingAverage.text = model.getAveragePain().toString()
        }
    }


    fun setupButtons(){
        binding.ViewMoreDetailsButton.setOnClickListener(){
            findNavController().navigate(R.id.nav_data)
        }
    }

    fun readUserFromFireStore() {
        var user = UserData()
        Firebase.firestore.collection(UserData.COLLECTION_PATH).document(Firebase.auth.uid!!).get().addOnSuccessListener { snapshot: DocumentSnapshot ->
            user = snapshot.toObject(UserData::class.java)!!
            binding.profileName.text = user.userName

        }

    }


    private fun drawChart(){
        //Create the graph
        chart = binding.PainTrackingDataGraph

        //Set Colors
        chart.setBackgroundColor(Color.WHITE)
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)

        //Set Graph Padding
        chart.extraTopOffset = -30f
        chart.extraBottomOffset = 10f
        chart.extraLeftOffset = 70f
        chart.extraRightOffset = 70f

        //Setup the chart description based on which type of data to record
        chart.description.isEnabled = false
       if(displayLastWeek){
           chart.description.text = "Weekly Data"
       }else{
           chart.description.text = "Monthly Data"
       }

        //Can't scale chart in any direction
        chart.setPinchZoom(false)
        //Don't draw grids
        chart.setDrawGridBackground(false)


        //Initialize the X axis formatting
        val xAxis = chart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textColor = Color.LTGRAY
        xAxis.textSize = 13f
        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 1f

        if(displayLastWeek){
            xAxis.labelCount = 7
        }else{
            xAxis.labelCount = 31
        }

        //Setup the axis itself and the formatting
        val left = chart.axisLeft
        left.setDrawLabels(false)
        left.spaceTop = 25f
        left.spaceBottom = 25f
        left.setDrawAxisLine(false)
        left.setDrawGridLines(false)
        left.setDrawZeroLine(true) // draw a zero line

        //Setup the zero line color and format
        left.zeroLineColor = resources.getColor(R.color.plum)
        left.zeroLineWidth = 1f
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false

        //Setup Data on Chart
        setupDataForChart()

    }


    private fun setupDataForChart() {
        //Grab the latest 7 data entries if in week mode, grab the latest 31 entries if in month mode

        val dataList = ArrayList<PainData>()
        if(displayLastWeek){
            // grab the last seven entries (if they exist)
            dataList.addAll(model.getSpecifedDataPoints(7))
        }else{
            dataList.addAll(model.getSpecifedDataPoints(31))
        }


//        dataList.add(Data(0f, -224.1f, "12-29"))
//        dataList.add(Data(1f, 238.5f, "12-30"))
//        dataList.add(Data(2f, 1280.1f, "12-31"))
//        dataList.add(Data(3f, -442.3f, "01-01"))
//        dataList.add(Data(4f, -2280.1f, "01-02"))


        val values = ArrayList<BarEntry>()
        val colors = ArrayList<Int>()
        val green = Color.rgb(110, 190, 102)
        val red = Color.rgb(211, 74, 88)


        for (i in dataList.indices) {
            //Flip to be negative and red for bad entries, and green and positive for good entries
            var dataValue = dataList[i].painLevel.toFloat();
            if(dataValue < 5){
                dataValue = - dataList[i].painLevel.toFloat();
            }

            values.add(BarEntry((dataList.size-i).toFloat(), dataValue))
            // specific colors, less than 5 is red, otherwise it is green
            if (dataList[i].painLevel < 5) colors.add(red) else colors.add(green)
        }

        //Take the data, and add it to the chart
        val set: BarDataSet
        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set = chart.data.getDataSetByIndex(0) as BarDataSet
            set.values = values
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            set = BarDataSet(values, "Values")
            set.colors = colors
            set.setValueTextColors(colors)
            val data = BarData(set)
            data.setValueTextSize(13f)
            //data.setValueFormatter(ValueFormatter()!!)

            data.barWidth = 0.8f
            chart.data = data
            chart.invalidate()
        }
    }





    private class ValueFormatter internal constructor() : IValueFormatter {
        private val mFormat: DecimalFormat
        override fun getFormattedValue(
            value: Float,
            entry: Entry,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler
        ): String {
            return mFormat.format(value.toDouble())
        }

        init {
            mFormat = DecimalFormat("######.0")
        }
    }


    override fun onStop() {
        super.onStop()
        model.removeListener(fragmentName)
    }

    companion object{
        const val fragmentName = "PainTrackingFragment"
    }





}