/**
 * This file is part of MythTV Android Frontend
 *
 * MythTV Android Frontend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MythTV Android Frontend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MythTV Android Frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This software can be found at <https://github.com/MythTV-Clients/MythTV-Android-Frontend/>
 */
package org.mythtv.client.ui.dvr;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.channel.ChannelInfo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class GuideFragment extends AbstractMythFragment implements GuideChannelFragment.OnChannelScrollListener {

	private static final String TAG = GuideFragment.class.getSimpleName();
	
	private TextView mProgramGuideDate;
	private GuideChannelFragment mGuideChannelFragment;
	private GuideTimeslotsFragment mGuideTimeslotsFragment;
	
	private DateTime today;
	private List<DateTime> dateRange = new ArrayList<DateTime>();
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		//inflate fragment layout
		View view = inflater.inflate( R.layout.fragment_dvr_guide, container, false );

		Log.v( TAG, "onCreateView : exit" );
		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );
		
		View view = getView();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( getActivity() );
		int downloadDays = Integer.parseInt( sp.getString( "preference_program_guide_days", "14" ) );

		today = new DateTime().withTimeAtStartOfDay();
		dateRange.add( today );
		for( int i = 1; i < downloadDays; i++ ) {
			dateRange.add( today.plusDays( i ) );
		}
		
		mProgramGuideDate = (TextView) getActivity().findViewById( R.id.program_guide_date );
		mProgramGuideDate.setText( DateUtils.getDateTimeUsingLocaleFormattingPrettyDateOnly( today, getMainApplication().getDateFormat() ) );
		
		// get child fragment manager
		FragmentManager manager = this.getChildFragmentManager();

		// look for program guide channels list placeholder frame layout
		FrameLayout channelsLayout = (FrameLayout) view.findViewById( R.id.frame_layout_program_guide_channels );
		if( null != channelsLayout ) {
			Log.v( TAG, "onActivityCreated : loading channels fragment" );
			
			mGuideChannelFragment = (GuideChannelFragment) manager.findFragmentByTag( GuideChannelFragment.class.getName() );
			if( null == mGuideChannelFragment ) {
				mGuideChannelFragment = (GuideChannelFragment) GuideChannelFragment.instantiate( getActivity(), GuideChannelFragment.class.getName() );
				mGuideChannelFragment.setOnChannelScrollListener( this );
			}

			manager.beginTransaction()
				.replace( R.id.frame_layout_program_guide_channels, mGuideChannelFragment, GuideChannelFragment.class.getName() )
				.commit();
		
		}

		FrameLayout timeslotsLayout = (FrameLayout) view.findViewById( R.id.frame_layout_program_guide_timeslots );
		if( null != timeslotsLayout ) {
			Log.v( TAG, "onActivityCreated : loading timeslots fragment" );
			
			mGuideTimeslotsFragment = (GuideTimeslotsFragment) manager.findFragmentByTag( GuideTimeslotsFragment.class.getName() );
			if( null == mGuideTimeslotsFragment ) {
				mGuideTimeslotsFragment = (GuideTimeslotsFragment) GuideTimeslotsFragment.instantiate( getActivity(), GuideTimeslotsFragment.class.getName() );
				//mGuideTimeslotsFragment.setOnTimeslotScrollListener( this );
			}

			manager.beginTransaction()
				.replace( R.id.frame_layout_program_guide_timeslots, mGuideTimeslotsFragment, GuideTimeslotsFragment.class.getName() )
				.commit();
		
		}

		Log.v( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.GuideChannelFragment.OnChannelScrollListener#channelScroll(int, int, int)
	 */
	@Override
	public void channelScroll( int first, int last, int screenCount, int totalCount ) {
		Log.v( TAG, "channelScroll : enter" );
		
		Log.v( TAG, "channelScroll : first=" + first + ", last=" + last + ", screenCount=" + screenCount + ", totalCount=" + totalCount );

		ChannelInfo start = mGuideChannelFragment.getChannel( first );
		Log.v( TAG, "channelScroll : start=" + start.toString() );

		ChannelInfo end = mGuideChannelFragment.getChannel( last );
		Log.v( TAG, "channelScroll : end=" + end.toString() );
		
		Log.v( TAG, "channelScroll : exit" );
	}

}
