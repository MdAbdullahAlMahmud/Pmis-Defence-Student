package com.mkrlabs.pmisstudent.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.mkrlabs.pmisstudent.adapter.MyPagerAdapter
import com.mkrlabs.pmisstudent.databinding.FragmentPrjecetDetailsBinding
import com.mkrlabs.pmisstudent.fragment.project_details_tab.ChatTabFragment
import com.mkrlabs.pmisstudent.fragment.project_details_tab.OverviewTabFragment
import com.mkrlabs.pmisstudent.fragment.project_details_tab.TeacherTaskAddFragment


class ProjectDetailsFragment : Fragment() {


    lateinit var binding: FragmentPrjecetDetailsBinding
    val   project : ProjectDetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrjecetDetailsBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MyPagerAdapter(childFragmentManager, lifecycle)
        adapter.addFragment(OverviewTabFragment(project.project.projectUID), "Overview")
        adapter.addFragment(TeacherTaskAddFragment(project.project.projectUID), "Task")
        adapter.addFragment(ChatTabFragment(project.project.projectUID), "Chat")

        binding.tabViewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.tabViewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        binding.projectDetailsBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.projectDetailsProjectName.text = project.project.projectName
    }
}