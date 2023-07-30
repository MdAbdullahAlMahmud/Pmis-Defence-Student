package com.mkrlabs.pmisstudent.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.databinding.FragmentInformationBinding
import com.mkrlabs.pmisstudent.model.InformationModel
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Resource
import com.mkrlabs.pmisstudent.view_model.InformationViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InformationFragment : Fragment() {


    private lateinit var binding : FragmentInformationBinding

    private lateinit var informationViewModel: InformationViewModel

    private  var myInformationModel: InformationModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInformationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        informationViewModel = ViewModelProvider(this)[InformationViewModel::class.java]

        getInitialData()
        setOnClickListener()
        setObserver()

    }

    private fun setOnClickListener(){

        binding.informationBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.devGithubCV.setOnClickListener {

            if (myInformationModel!=null){
                openThirdPartyUrl(myInformationModel!!.devGithub)
            }else{
                CommonFunction.errorToast(requireContext(),"Something went wrong. Try again later")
            }

        }

        binding.devLinkedInCV.setOnClickListener {

            if (myInformationModel!=null){
                openThirdPartyUrl(myInformationModel!!.devLinkedIn)
            }else{
                CommonFunction.errorToast(requireContext(),"Something went wrong. Try again later")
            }

        }

        binding.devEmailCV.setOnClickListener {

            if (myInformationModel!=null && myInformationModel!!.devEmail.isNotEmpty()){
                val email = myInformationModel!!.devEmail
                val emailList = listOf<String?>(email)
                val subject = "connect with developer"
                composeEmail(emailList.toTypedArray(),subject)
            }else{
                CommonFunction.errorToast(requireContext(),"Something went wrong. Try again later")
            }


        }
    }
    private  fun getInitialData(){
        informationViewModel.getProjectInformation()

    }
    private fun setObserver(){
        informationViewModel.informationModelState.observe(viewLifecycleOwner, Observer {response ->
            when(response){

                is Resource.Success->{
                    hideLoading()
                    response.data?.let {
                        myInformationModel = it
                        val informationModel = it
                        binding.developerName.text = informationModel.devName
                        binding.developerVersityName.text = informationModel.devVersityName
                        binding.developerVersityId.text = informationModel.devVersityId

                        Glide.with(requireContext())
                            .load(informationModel.devImage)
                            .placeholder(R.drawable.empty_person)
                            .error(R.drawable.empty_person)
                            .into(binding.developerImage)


                    }

                }

                is Resource.Loading->{
                    showLoading()

                }

                is Resource.Error->{
                    hideLoading()
                    CommonFunction.errorToast(requireContext(),response.message.toString())
                }
            }
        })

    }
    fun hideLoading(){
        binding.informationProgressBar.visibility= View.GONE
    }

    fun showLoading(){
        binding.informationProgressBar.visibility= View.VISIBLE
    }

    fun composeEmail(addresses: Array<String?>?, subject: String?) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            CommonFunction.errorToast(requireContext(),"No email client found")
        }
    }

    fun openThirdPartyUrl( link  : String){
        var url = link
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (browserIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(browserIntent)
        } else {
            CommonFunction.errorToast(requireContext(),"No Web client found")

        }
    }
}