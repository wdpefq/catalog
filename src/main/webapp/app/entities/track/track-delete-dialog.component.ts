import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITrack } from 'app/shared/model/track.model';
import { TrackService } from './track.service';

@Component({
    selector: 'jhi-track-delete-dialog',
    templateUrl: './track-delete-dialog.component.html'
})
export class TrackDeleteDialogComponent {
    track: ITrack;

    constructor(private trackService: TrackService, public activeModal: NgbActiveModal, private eventManager: JhiEventManager) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.trackService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'trackListModification',
                content: 'Deleted an track'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-track-delete-popup',
    template: ''
})
export class TrackDeletePopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ track }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(TrackDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.track = track;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
