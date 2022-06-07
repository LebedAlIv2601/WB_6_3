package com.example.wb_6_3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.wb_6_3.databinding.FragmentTopBinding
import com.example.wb_6_3.vm.SharedViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.lang.StringBuilder


class TopFragment : Fragment() {

    private var binding: FragmentTopBinding? = null

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var numberOfIterations = 0

    private var isCounting = false

    private var pi = "0"

    private var isResetCounting = false

    private val countJob = Job()
    private val countScope = CoroutineScope(Dispatchers.Main + countJob)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTopBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        binding?.piTextView?.text
    }

    private suspend fun countPI(){
        while (isCounting) {

            withContext(Dispatchers.IO) {
                piSpigot(numberOfIterations).collect { newPi -> pi = newPi }
            }

            numberOfIterations++


            sharedViewModel.setNumberOfIterations(numberOfIterations)
            sharedViewModel.setPi(pi)
            binding?.piTextView?.text = pi

        }
        if (isResetCounting) {
            binding?.piTextView?.text = "0"
            sharedViewModel.setIsReset(false)

            isResetCounting = false
        }
    }

    private fun setupObservers() {
        sharedViewModel.isPICounting.observe(viewLifecycleOwner, Observer {
            isCounting = it
            if (it) {
                countScope.launch {
                    countPI()
                }
            }
        })

        sharedViewModel.pi.observe(viewLifecycleOwner, Observer {
            pi = it
        })

        sharedViewModel.numberOfIterations.observe(viewLifecycleOwner, Observer {
            numberOfIterations = it
        })

        sharedViewModel.isReset.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (!isCounting) {
                    binding?.piTextView?.text = "0"
                    sharedViewModel.setIsReset(false)
                } else {
                    isResetCounting = true
                    sharedViewModel.setIsPICounting(false)
                    sharedViewModel.setIsReset(false)
                }
            }
        })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


    //Взятый из интернета способ вычисления числа Пи
    private fun piSpigot(n: Int) = flow{

            // найденные цифры сразу же будем записывать в StringBuilder
            val pi = StringBuilder(n)
            val boxes = n * 10 / 3 // размер массива
            val reminders = IntArray(boxes)
            // инициализируем масив двойками
            for (i in 0 until boxes) {
                reminders[i] = 2
            }
            var heldDigits = 0 // счётчик временно недействительных цифр
            for (i in 0 until n) {
                var carriedOver = 0 // перенос на следующий шаг
                var sum = 0
                for (j in boxes - 1 downTo 0) {
                    reminders[j] *= 10
                    sum = reminders[j] + carriedOver
                    val quotient = sum / (j * 2 + 1) // результат деления суммы на знаменатель
                    reminders[j] = sum % (j * 2 + 1) // остаток от деления суммы на знаменатель
                    carriedOver = quotient * j // j - числитель
                }
                reminders[0] = sum % 10
                var q = sum / 10 // новая цифра числа Пи
                // регулировка недействительных цифр
                if (q == 9) {
                    heldDigits++
                } else if (q == 10) {
                    q = 0
                    for (k in 1..heldDigits) {
                        var replaced = pi.substring(i - k, i - k + 1).toInt()
                        if (replaced == 9) {
                            replaced = 0
                        } else {
                            replaced++
                        }
                        pi.deleteCharAt(i - k)
                        pi.insert(i - k, replaced)
                    }
                    heldDigits = 1
                } else {
                    heldDigits = 1
                }
                pi.append(q) // сохраняем найденную цифру
            }
            if (pi.length >= 2) {
                pi.insert(1, '.') // добавляем в строчку точку после 3
            }
            emit(pi.toString())

    }

}