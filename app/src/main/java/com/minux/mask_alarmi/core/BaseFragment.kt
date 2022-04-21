package com.minux.mask_alarmi.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<B: ViewDataBinding>(@LayoutRes val layoutRes: Int): Fragment() {
    private lateinit var binding: B

    protected abstract fun initDataBinding()
    protected abstract fun initAfterBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        initDataBinding()
        initAfterBinding()

        return binding.root
    }
}