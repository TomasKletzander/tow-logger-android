package cz.dmn.towlogger.ui.pickpilot

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cz.dmn.towlogger.R
import cz.dmn.towlogger.core.io.api.models.Pilot
import cz.dmn.towlogger.databinding.ActivityPickPilotBinding
import cz.dmn.towlogger.databinding.PilotItemBinding
import cz.dmn.towlogger.ui.BaseActivity
import javax.inject.Inject

class PickPilotActivity : BaseActivity() {

    companion object {
        val EXTRA_PILOT_TYPE = "PILOT_TYPE"
        val PILOT_TYPE_GLD = "gld"
        val PILOT_TYPE_TOW = "tow"
        val EXTRA_PILOT_NAME = "PILOT_NAME"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: PickPilotViewModel
    lateinit var adapter: PilotsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityPickPilotBinding>(this, R.layout.activity_pick_pilot)
        setSupportActionBar(binding.toolbar)
        val pilotType = intent.getStringExtra(EXTRA_PILOT_TYPE)
        val title = if (pilotType == null) R.string.titlePickPilot else if (pilotType.equals(PILOT_TYPE_TOW)) R.string.titlePickTowPilot else R.string.titlePickGldPilot
        supportActionBar?.setTitle(title)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PickPilotViewModel::class.java)
        viewModel.showTow = pilotType == null || pilotType.equals(PILOT_TYPE_TOW)
        viewModel.showGld = pilotType == null || pilotType.equals(PILOT_TYPE_GLD)
        viewModel.error.observe(this, Observer {
            it?.apply {
                if (this) showError(R.string.errorLoadingPilotsData)
            }
        })

        adapter = PilotsListAdapter()
        binding.viewModel = viewModel
        binding.adapter = adapter
        viewModel.pilots.observe(this, Observer {
            adapter.notifyDataSetChanged()
        })
    }

    class PilotViewHolder(private val binding: PilotItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pilot: Pilot) {
            binding.name = pilot.name
        }
    }

    inner class PilotsListAdapter : RecyclerView.Adapter<PilotViewHolder>() {

        override fun getItemCount() = viewModel.pilots.value?.size ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilotViewHolder {
            val binding = DataBindingUtil.inflate<PilotItemBinding>(LayoutInflater.from(
                    this@PickPilotActivity), R.layout.pilot_item, parent, false)
            binding.setClickListener {
                setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_PILOT_NAME, binding.name))
                finishAfterTransition()
            }
            return PilotViewHolder(binding)
        }

        override fun onBindViewHolder(holder: PilotViewHolder, position: Int) =
                viewModel.pilots.value?.get(position)?.let {
                    holder.bind(it)
                } ?: Unit
    }
}
