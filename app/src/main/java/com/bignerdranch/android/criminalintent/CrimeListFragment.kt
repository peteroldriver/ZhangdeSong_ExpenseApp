package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeListBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentCrimeListBinding? = null
    private val args: CrimeListFragmentArgs by navArgs()
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val crimeListViewModel: CrimeListViewModel by viewModels()
    private lateinit var categoryList: List<String>
    private lateinit var date: Date
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "OnCreate()")
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "OnCreateView()")
        _binding = FragmentCrimeListBinding.inflate(inflater, container, false)
        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        categoryList = resources.getStringArray(R.array.expense_category).toList()
        var l1  = listOf<String>("Default", "ALL")
        categoryList = l1+categoryList
        //Spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.expense_category2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            binding.crimeCategorySearch.adapter = adapter
            if(args.dateSelected != null){
                crimeListViewModel.collectCrimesByDate(args.dateSelected as Date)
            }
            else{
                if(args.type == "ALL"){
                    crimeListViewModel.init1()
                }
                else(crimeListViewModel.collectCrimesByCategory(args.type))
            }
        }
        binding.crimeCategorySearch.onItemSelectedListener = this
        //Date Selector
        date = Date()

        binding.crimeDateSearch.setOnClickListener {
            findNavController().navigate(
                CrimeListFragmentDirections.actionCrimeListFragmentToDatePickerFragment(date)
            )
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { _, bundle ->
            val newDate =
                bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            if(date == newDate){Log.d("Date Debug", "Same Date Selected")}
            else{
                crimeListViewModel.collectCrimesByDate(newDate)
                Log.d("Date Debug", "Date Selected$newDate")
                findNavController().navigate(DatePickerFragmentDirections.actionDatePickerFragmentToCrimeListFragment("ALL", newDate))
            }
        }

        binding.crimeDateCancel.setOnClickListener {
            crimeListViewModel.init1()
            findNavController().navigate(CrimeListFragmentDirections.actionCrimeListFragmentSelf("ALL",null))
        }
        binding.crimeDateSearch.text = "SELECT DATE"
        binding.crimeDateCancel.text = "ClEAR SELECTION"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG", "OnViewCreated()")
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeListViewModel.crimes.collect { crimes ->
                    Log.d("TAGF", crimes.toString())
                    binding.crimeRecyclerView.adapter =
                        CrimeListAdapter(crimes) { crimeId ->
                            findNavController().navigate(
                                CrimeListFragmentDirections.showCrimeDetail(crimeId)
                            )
                        }
                }
            }
//            Log.d("TAG", "OnCreat2")
        }
//        Log.d("TAG", "OnCreat3")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
        Log.d("TAG", "Menu1")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                showNewCrime()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showNewCrime() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newCrime = Crime(
                id = UUID.randomUUID(),
                title = "",
                date = Date(),
                category = "MISC",
                amount = 0
            )
            crimeListViewModel.addCrime(newCrime)
            findNavController().navigate(
                CrimeListFragmentDirections.showCrimeDetail(newCrime.id)
            )
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d("TAG", "{$categoryList.toString()}")
        Log.d("TAG", "Spinner Selected: Index: $position : ${categoryList[position]}")
        if(position == 0){}
        else if(position == 1){
            crimeListViewModel.init1()
            findNavController().navigate(CrimeListFragmentDirections.actionCrimeListFragmentSelf(categoryList[position],null))
        }
        else {
            crimeListViewModel.collectCrimesByCategory(categoryList[position])
            findNavController().navigate(CrimeListFragmentDirections.actionCrimeListFragmentSelf(categoryList[position],null))
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


}
