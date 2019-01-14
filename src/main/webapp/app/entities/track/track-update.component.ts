import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ITrack } from 'app/shared/model/track.model';
import { TrackService } from './track.service';

@Component({
    selector: 'jhi-track-update',
    templateUrl: './track-update.component.html'
})
export class TrackUpdateComponent implements OnInit {
    private _track: ITrack;
    isSaving: boolean;

    constructor(private trackService: TrackService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ track }) => {
            this.track = track;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.track.id !== undefined) {
            this.subscribeToSaveResponse(this.trackService.update(this.track));
        } else {
            this.subscribeToSaveResponse(this.trackService.create(this.track));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ITrack>>) {
        result.subscribe((res: HttpResponse<ITrack>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
    get track() {
        return this._track;
    }

    set track(track: ITrack) {
        this._track = track;
    }
}
