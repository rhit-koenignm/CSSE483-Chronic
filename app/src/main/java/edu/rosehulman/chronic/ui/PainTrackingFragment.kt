package edu.rosehulman.chronic.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import edu.rosehulman.chronic.databinding.FragmentPaintrackingBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import edu.rosehulman.chronic.R


class PainTrackingFragment : Fragment() {

    private lateinit var binding: FragmentPaintrackingBinding



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaintrackingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setuButtons()
        drawLineChart();
        return root
    }



    fun setuButtons(){
        binding.ViewMoreDetailsButton.setOnClickListener(){
            findNavController().navigate(R.id.nav_data)
        }
    }


    fun drawLineChart(){
        var lineChart: LineChart = binding.PainTrackingDataGraph
        var lineEntries: ArrayList<Entry> = getDataSet();
        var lineDataSet: LineDataSet = LineDataSet(lineEntries, "Main")

        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT)
        lineDataSet.lineWidth = 2F
        lineDataSet.setDrawValues(false)
        lineDataSet.color = Color.CYAN
        lineDataSet.circleRadius = 6F
        lineDataSet.circleHoleRadius = 3F
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setHighLightColor(Color.CYAN);
        lineDataSet.valueTextSize = 12F
        lineDataSet.setValueTextColor(Color.DKGRAY);
        lineDataSet.setMode(LineDataSet.Mode.STEPPED);

        val lineData: LineData = LineData(lineDataSet)
        lineChart.getDescription().textSize = 12F
        lineChart.getDescription().setEnabled(false)
        lineChart.animateY(1000)
        lineChart.setData(lineData)

        // Setup X Axis
        // Setup X Axis
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 1.0f
        xAxis.xOffset = 1f
        xAxis.labelCount = 25
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 24f

        // Setup Y Axis

        // Setup Y Axis
        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 3f
        yAxis.granularity = 1f

        val yAxisLabel: ArrayList<String> = ArrayList()
        yAxisLabel.add(" ")
        yAxisLabel.add("Rest")
        yAxisLabel.add("Work")
        yAxisLabel.add("2-up")

        lineChart.axisLeft.setCenterAxisLabels(true)
        lineChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return if (value == -1f || value >= yAxisLabel.size) "" else yAxisLabel[value.toInt()]
            }
        }

        lineChart.axisRight.isEnabled = false
        lineChart.invalidate()

    }

    fun getDataSet():ArrayList<Entry> {
        var lineEntries: ArrayList<Entry> = ArrayList<Entry>()
        lineEntries.add(Entry(0F, 1F));
        lineEntries.add(Entry(1F, 1F));
        lineEntries.add(Entry(2F, 1F));
        lineEntries.add(Entry(3F, 1F));
        lineEntries.add(Entry(4F, 1F));
        lineEntries.add(Entry(5F, 1F));

        lineEntries.add(Entry(6F, 2F));
        lineEntries.add(Entry(7F, 2F));
        lineEntries.add(Entry(8F, 2F));
        lineEntries.add(Entry(9F, 2F));
        lineEntries.add(Entry(10F, 2F));

        lineEntries.add(Entry(11F, 1F));
        lineEntries.add(Entry(12F, 1F));

        lineEntries.add(Entry(13F, 2F));
        lineEntries.add(Entry(14F, 2F));
        lineEntries.add(Entry(15F, 2F));

        lineEntries.add(Entry(16F, 1F));
        lineEntries.add(Entry(17F, 1F));

        lineEntries.add(Entry(18F, 2F));
        lineEntries.add(Entry(19F, 2F));
        lineEntries.add(Entry(20F, 2F));
        lineEntries.add(Entry(21F, 2F));

        lineEntries.add(Entry(22F, 1F));
        lineEntries.add(Entry(23F, 1F));
        lineEntries.add(Entry(24F, 1F));
        return lineEntries;
    }





}