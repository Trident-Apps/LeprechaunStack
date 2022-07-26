package com.taptri.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.taptri.R
import com.taptri.databinding.LoadingFragmentBinding
import com.taptri.util.Checkers
import com.taptri.viewmodel.LeprechaunViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadingFragment : Fragment() {

    private val TAG = "LeprechaunViewModel"
    private var _binding: LoadingFragmentBinding? = null
    private val binding get() = _binding
    private val mCheckers = Checkers()
    private lateinit var leprechaunViewModel: LeprechaunViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoadingFragmentBinding.inflate(inflater, container, false)

        leprechaunViewModel =
            ViewModelProvider(this@LoadingFragment).get(LeprechaunViewModel::class.java)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "On Create View")

        if (!mCheckers.isDeviceSecured(this@LoadingFragment.requireActivity())) {

            Log.d(TAG, "isSecured")
            lifecycleScope.launch(Dispatchers.IO) {

                leprechaunViewModel.getDeepLink(this@LoadingFragment.requireActivity())
                Log.d(TAG, "init after")

                lifecycleScope.launch(Dispatchers.Main) {
                    leprechaunViewModel.urlLiveData.observe(viewLifecycleOwner) {
                        Log.d(TAG, "Appsflyer init")
                        startWebView(it)
                    }
                }

            }
        } else {
            startGame()
        }

    }

    private fun startGame() {
        findNavController().navigate(R.id.gameFragment)
    }

    private fun startWebView(url: String) {
        val bundle = bundleOf("url" to url)
        findNavController().navigate(R.id.webViewFragment, bundle)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}