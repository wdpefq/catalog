import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable } from 'rxjs';
import { Track } from 'app/shared/model/track.model';
import { TrackService } from './track.service';
import { TrackComponent } from './track.component';
import { TrackDetailComponent } from './track-detail.component';
import { TrackUpdateComponent } from './track-update.component';
import { TrackDeletePopupComponent } from './track-delete-dialog.component';
import { ITrack } from 'app/shared/model/track.model';

@Injectable({ providedIn: 'root' })
export class TrackResolve implements Resolve<ITrack> {
    constructor(private service: TrackService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).map((track: HttpResponse<Track>) => track.body);
        }
        return Observable.of(new Track());
    }
}

export const trackRoute: Routes = [
    {
        path: 'track',
        component: TrackComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Tracks'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'track/:id/view',
        component: TrackDetailComponent,
        resolve: {
            track: TrackResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Tracks'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'track/new',
        component: TrackUpdateComponent,
        resolve: {
            track: TrackResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Tracks'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'track/:id/edit',
        component: TrackUpdateComponent,
        resolve: {
            track: TrackResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Tracks'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const trackPopupRoute: Routes = [
    {
        path: 'track/:id/delete',
        component: TrackDeletePopupComponent,
        resolve: {
            track: TrackResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Tracks'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
