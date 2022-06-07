package com.example.wb_6_3

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import com.cesarferreira.tempo.*
import com.example.wb_6_3.databinding.FragmentBottomBinding
import com.example.wb_6_3.vm.SharedViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.*


class BottomFragment : Fragment() {

    private var binding: FragmentBottomBinding? = null

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var time = Tempo.with(minute = 0, second = 0)

    private var isCountingTime = false

    private val timerJob = Job()
    private val timerScope = CoroutineScope(Dispatchers.Main + timerJob)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBottomBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()


    }

    private val timeFlow = flow {
        Log.e("Thread", Thread.currentThread().toString())
        while (isCountingTime) {
            delay(1000L)
            if(isCountingTime) {
                val newTime = time + 1.second
                emit(newTime)
            }
        }
    }

    private suspend fun setupTimer() {

        timeFlow.collect {newTime ->
            changeBackgroundColorWithTime(newTime)

            binding?.timeTextView?.text = newTime.toString("mm:ss")
            time = newTime
        }
    }

    private fun setupListeners() {

        binding?.apply {
            buttonPlay.setOnClickListener {
                isCountingTime = if (!isCountingTime) {
                    buttonPlay.setImageResource(R.drawable.ic_baseline_pause_24)
                    sharedViewModel.setIsPICounting(true)
                    timerScope.launch {
                        setupTimer()
                    }
                    true
                } else {
                    buttonPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    sharedViewModel.setIsPICounting(false)
                    false
                }
            }

            buttonReset.setOnClickListener {
                time = Tempo.with(minute = 0, second = 0)
                timeTextView.text = time.toString("mm:ss")
                isCountingTime = false
                sharedViewModel.setPi("0")
                sharedViewModel.setNumberOfIterations(0)
                sharedViewModel.setIsReset(true)
                buttonPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                bottomFragmentLayout.setBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.white,
                        null
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun changeBackgroundColorWithTime(newTime: Date) {

        if ((newTime.toString("ss").toInt() + newTime.toString("mm").toInt() * 60)
            % 40 == 0) {
            binding?.bottomFragmentLayout?.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.white,
                    null
                )
            )
        } else if ((newTime.toString("ss").toInt() + newTime.toString("mm")
                .toInt() * 60) % 20 == 0
        ) {
            binding?.bottomFragmentLayout?.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.purple_200,
                    null
                )
            )
        }
    }

}