package com.mkrlabs.pmisstudent.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.databinding.FragmentLoginBinding
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Resource
import com.mkrlabs.pmisstudent.util.SharedPref
import com.mkrlabs.pmisstudent.view_model.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {


    lateinit var binding: FragmentLoginBinding

    lateinit var authViewModel : AuthenticationViewModel


    val TAG : String = "Auth"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]

        binding.donthaveAnAccountTV.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.loginButton.setOnClickListener {


            val email = binding.loginEmailEdt.text.toString()
            val password = binding.loginPassword.text.toString()



            if (TextUtils.isEmpty(email)){
                binding.loginEmailEdt.error= "required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)){
                binding.loginPassword.error= "required"
                return@setOnClickListener
            }


            authViewModel.loginUser(email,password)

        }

        /**
         * Login State Observe
         */

        authViewModel.loginState.observe(viewLifecycleOwner, Observer { response->

            when(response){
                is  Resource.Success->{
                    hideLoading()
                    response.data?.let { result ->
                        authViewModel.userWithUIDData.postValue(Resource.Loading())
                        authViewModel.getSpecificUserWithUID(result.uid)

                    }
                }
                is Resource.Error->{
                    hideLoading()
                    response.message?.let {message->

                        CommonFunction.errorToast(view.context,"Invalid credential")

                        Log.e(TAG,"An error occured $message")
                    }
                }

                is  Resource.Loading->{
                    showLoading()
                }

            }
        })

        authViewModel.userWithUIDData.observe(viewLifecycleOwner, Observer {response->
            when(response){
                is  Resource.Success->{
                    hideLoading()
                    response.data?.let { result ->
                        val sharedPref = SharedPref(view.context)
                        sharedPref.setLoggedInUserName(result.name)
                        CommonFunction.successToast(view.context,"Logged In successfully")
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)

                    }
                }
                is Resource.Error->{
                    hideLoading()
                    response.message?.let {message->
                        CommonFunction.errorToast(view.context,"Something went wrong")
                        Log.e(TAG,"An error occured $message")
                    }
                }

                is  Resource.Loading->{
                    showLoading()
                }

            }
        })

    }

    fun hideLoading(){
        binding.loginProgressBar.visibility= View.GONE
    }

    fun showLoading(){
        binding.loginProgressBar.visibility= View.VISIBLE
    }

}